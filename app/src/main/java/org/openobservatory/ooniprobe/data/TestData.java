package org.openobservatory.ooniprobe.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.android.DnsUtils;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.*;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import org.openobservatory.ooniprobe.model.UnknownTest;
import org.openobservatory.ooniprobe.utils.NotificationHandler;
import org.openobservatory.ooniprobe.utils.VersionUtils;

public class TestData extends Observable {
    private static final String TAG = "TestData";
    private static TestData instance;
    public static ArrayList<NetworkMeasurement> runningTests;
    public static ArrayList<NetworkMeasurement> finishedTests;
    public static LinkedHashMap<String, Boolean> availableTests;
    public static Activity activity;
    public static Context context;

    public static TestData getInstance(final Context c, final Activity a) {
        if (instance == null) {
            context = c;
            activity = a;
            instance = new TestData();
            runningTests = new ArrayList<>();
            finishedTests = TestStorage.loadTests(context);
            availableTests = new LinkedHashMap<>();
            availableTests.put(OONITests.WEB_CONNECTIVITY, true);

            availableTests.put(OONITests.FACEBOOK_MESSENGER, true);
            availableTests.put(OONITests.TELEGRAM, true);
            availableTests.put(OONITests.WHATSAPP, true);

            availableTests.put(OONITests.HTTP_HEADER_FIELD_MANIPULATION, true);
            availableTests.put(OONITests.HTTP_INVALID_REQUEST_LINE, true);

            availableTests.put(OONITests.DASH, true);
            availableTests.put(OONITests.NDT, true);
        }
        else if (activity == null && a != null){
            activity = a;
        }
        return instance;
    }

    public static NetworkMeasurement configureTest(final Context ctx, final NetworkMeasurement currentTest) {
        final String outputPath = ctx.getFilesDir() + "/"  + currentTest.json_file;
        final String logPath = ctx.getFilesDir() + "/"  + currentTest.log_file;

        final String geoip_asn = ctx.getFilesDir() + "/GeoIPASNum.dat";
        final String geoip_country = ctx.getFilesDir() + "/GeoIP.dat";

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final Boolean include_ip = preferences.getBoolean("include_ip", false);
        final Boolean include_asn = preferences.getBoolean("include_asn", true);
        final Boolean include_cc = preferences.getBoolean("include_cc", true);
        final Boolean upload_results = preferences.getBoolean("upload_results", true);

        currentTest.test.use_logcat();
        currentTest.test.set_output_filepath(outputPath);
        currentTest.test.set_error_filepath(logPath);
        currentTest.test.set_verbosity(OONITests.MK_VERBOSITY);
        currentTest.test.set_options("geoip_country_path", geoip_country);
        currentTest.test.set_options("geoip_asn_path", geoip_asn);
        currentTest.test.set_options("save_real_probe_ip", boolToString(include_ip));
        currentTest.test.set_options("save_real_probe_asn", boolToString(include_asn));
        currentTest.test.set_options("save_real_probe_cc", boolToString(include_cc));
        currentTest.test.set_options("no_collector", boolToString(!upload_results));
        currentTest.test.set_options("software_name", "ooniprobe-android");
        currentTest.test.set_options("software_version", VersionUtils.get_software_version());
        currentTest.test.on_progress(new org.openobservatory.measurement_kit.nettests.ProgressCallback() {
            @Override
            public void callback(double percent, String msg) {
                currentTest.progress = (int)(percent*100);
                if (activity != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TestData.getInstance(context, activity).notifyObservers();
                        }
                    });
                }
            }
        });
        return currentTest;
    }

    public static void doNetworkMeasurements(final Context ctx, final NetworkMeasurement currentTest) {
        configureTest(ctx, currentTest);
        TestStorage.addTest(ctx, currentTest);
        runningTests.add(currentTest);
        availableTests.put(currentTest.testName, false);
        if (activity != null) TestData.getInstance(context, activity).notifyObservers();

        Log.v(TAG, "doNetworkMeasurements " + currentTest.testName + "...");
        /*
        Using AsyncTask  may not be the optimal solution since OONI tests could take a long time to complete
        For more info read: http://developer.android.com/reference/android/os/AsyncTask.html
        https://developer.android.com/reference/android/support/v4/os/AsyncTaskCompat.html
        */
        AsyncTaskCompat.executeParallel(
                new AsyncTask<String, String, Boolean>(){
                    @Override
                    protected Boolean doInBackground(String... params)
                    {
                        try
                        {
                            Log.v(TAG, "running test...");
                            if (currentTest.testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                                Log.v(TAG, "running http_invalid_request_line test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomalyHirl(entry, currentTest);
                                            }
                                        }).run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.HTTP_HEADER_FIELD_MANIPULATION) == 0) {
                                Log.v(TAG, "running http_header_field_manipulation test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomalyHhfm(entry, currentTest);
                                            }
                                        })
                                        .run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.WEB_CONNECTIVITY) == 0) {
                                Log.v(TAG, "running web-connectivity test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomalyWc(entry, currentTest);
                                            }
                                        }).run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.NDT) == 0) {
                                Log.v(TAG, "running ndt test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomalyNdt(entry, currentTest);
                                            }
                                        })
                                        .run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.DASH) == 0) {
                                Log.v(TAG, "running dash test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomalyNdt(entry, currentTest);
                                            }
                                        })
                                        .run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.WHATSAPP) == 0) {
                                Log.v(TAG, "running whatsapp test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                    @Override
                                    public void callback(String entry) {
                                        setAnomalyWhatsapp(entry, currentTest);
                                    }
                                })
                                        .run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.TELEGRAM) == 0) {
                                Log.v(TAG, "running telegram test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                    @Override
                                    public void callback(String entry) {
                                        setAnomalyTelegram(entry, currentTest);
                                    }
                                })
                                        .run();
                            }
                            else if (currentTest.testName.compareTo(OONITests.FACEBOOK_MESSENGER) == 0) {
                                Log.v(TAG, "running facebook_messenger test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                    @Override
                                    public void callback(String entry) {
                                        setAnomalyFacebookMessenger(entry, currentTest);
                                    }
                                })
                                        .run();
                            }
                            else {
                                throw new UnknownTest(currentTest.testName);
                            }
                            Log.v(TAG, "running test... done");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }

                    protected void onProgressUpdate(String... values) {
                        /* Nothing */
                    }

                    protected void onPostExecute(Boolean success) {
                        TestStorage.setCompleted(ctx, currentTest);
                        currentTest.entry = true;
                        runningTests.remove(currentTest);
                        finishedTests.add(currentTest);
                        availableTests.put(currentTest.testName, true);
                        if (activity != null) TestData.getInstance(context, activity).notifyObservers(currentTest.testName);
                        NotificationHandler.notifyTestEnded(ctx, currentTest.testName);
                        Log.v(TAG, "doNetworkMeasurements " + currentTest.testName + "... done");
                    }
                }
        );
    }

    //TODO unify all these in an unique function that takes the test name.
    public static void setAnomalyWc(String entry, NetworkMeasurement test){
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            Object blocking = test_keys.get("blocking");
            if(blocking instanceof String)
                anomaly = OONITests.ANOMALY_RED;
            else if(blocking instanceof Boolean)
                anomaly = OONITests.ANOMALY_GREEN;
            else
                anomaly = OONITests.ANOMALY_ORANGE;
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
        }
    }

    /*
     on_entry method for http invalid request line test
     if the "tampering" key exists and is null then anomaly will be set to 1 (orange)
     otherwise "tampering" object exists and is TRUE, then anomaly will be set to 2 (red)
     */
    public static void setAnomalyHirl(String entry, NetworkMeasurement test){
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            if (test_keys.has("tampering")) {
                Boolean tampering = test_keys.getBoolean("tampering");
                if (tampering == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
                else if (tampering == true)
                    anomaly = OONITests.ANOMALY_RED;
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
        }
    }

    /*
    on_entry method for http invalid request line test
    if the "failure" key exists and is not null then anomaly will be set to 1 (orange)
    otherwise the keys in the "tampering" object will be checked, if any of them is TRUE, then anomaly will be set to 2 (red)
    */
    public static void setAnomalyHhfm(String entry, NetworkMeasurement test){
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            Object failure = test_keys.get("failure");
            if(failure == null)
                anomaly = OONITests.ANOMALY_ORANGE;
            else {
                JSONObject tampering = test_keys.getJSONObject("tampering");
                String keys[] = {"header_field_name",
                                 "header_field_number",
                                 "header_field_value",
                                 "header_name_capitalization",
                                 "request_line_capitalization",
                                 "total"};
                for (String key: keys)
                {
                    if (tampering.has(key))
                        if (tampering.getBoolean(key))
                            anomaly = OONITests.ANOMALY_RED;
                }
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSONException "+ e);
        }
    }

    /*
    on_entry method for ndt and dash test
    if the "failure" key exists and is not null then anomaly will be set to 1 (orange)
    */
    public static void setAnomalyNdt(String entry, NetworkMeasurement test){
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            if (test_keys.has("failure")) {
                Object failure = test_keys.get("failure");
                if (failure == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSONException "+ e);
        }
    }

    /*
     whatsapp: red if "whatsapp_endpoints_status" or "whatsapp_web_status" or "registration_server" are "blocked"
     docs: https://github.com/TheTorProject/ooni-spec/blob/master/test-specs/ts-018-whatsapp.md#semantics
     */
    public static void setAnomalyWhatsapp(String entry, NetworkMeasurement test) {
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            String keys[] = {"whatsapp_endpoints_status",
                    "whatsapp_web_status",
                    "registration_server_status"};
            for (String key: keys)
            {
                String value = test_keys.getString(key);
                if (value == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
                else if (value.equals("blocked"))
                    anomaly = OONITests.ANOMALY_RED;
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSONException "+ e);
        }
    }

    /*
    for telegram: red if either "telegram_http_blocking" or "telegram_tcp_blocking" is true, OR if ""telegram_web_status" is "blocked"
    the "*_failure" keys for telegram and whatsapp might indicate a test failure / anomaly
    docs: https://github.com/TheTorProject/ooni-spec/blob/master/test-specs/ts-020-telegram.md#semantics
    */
    public static void setAnomalyTelegram(String entry, NetworkMeasurement test) {
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            String keys[] = {"telegram_http_blocking",
                    "telegram_tcp_blocking"};
            for (String key: keys)
            {
                Object value = test_keys.get(key);
                Boolean boolvalue = test_keys.getBoolean(key);
                if (value == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
                else if (boolvalue)
                    anomaly = OONITests.ANOMALY_RED;
            }
            if (test_keys.has("telegram_web_status")) {
                String telegram_web_status = test_keys.getString("telegram_web_status");
                if (telegram_web_status == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
                else if (telegram_web_status.equals("blocked"))
                    anomaly = OONITests.ANOMALY_RED;
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSONException "+ e);
        }


    }

    /*
    FB: red blocking if either "facebook_tcp_blocking" or "facebook_dns_blocking" is true
    docs: https://github.com/TheTorProject/ooni-spec/blob/master/test-specs/ts-019-facebook-messenger.md#semantics
    */
    public static void setAnomalyFacebookMessenger(String entry, NetworkMeasurement test) {
        if(!test.entry) {
            TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = OONITests.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            String keys[] = {"facebook_tcp_blocking",
                    "facebook_dns_blocking"};
            for (String key: keys)
            {
                Object value = test_keys.get(key);
                Boolean boolvalue = test_keys.getBoolean(key);
                if (value == null)
                    anomaly = OONITests.ANOMALY_ORANGE;
                else if (boolvalue)
                    anomaly = OONITests.ANOMALY_RED;
            }
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSONException "+ e);
        }
    }



    @Override
    public void notifyObservers(Object type) {
        setChanged(); // Set the changed flag to true, otherwise observers won't be notified.
        super.notifyObservers(type);
    }

    public NetworkMeasurement getTestWithName(String name) {
        for (int j = 0; j < TestData.getInstance(context, activity).runningTests.size(); j++) {
            NetworkMeasurement current = TestData.getInstance(context, activity).runningTests.get(j);
            if (current.testName.equals(name)) return current;
        }
    return null;
    }

    public void removeTest(NetworkMeasurement test) {
        if (finishedTests != null){
            for(int i = 0; i < finishedTests.size(); i++) {
                NetworkMeasurement n = finishedTests.get(i);
                if (n.test_id == test.test_id) {
                    finishedTests.remove(i);
                    break;
                }
            }
        }
    }

    private static String boolToString(Boolean b) {
        return b ? "1" : "0";
    }
}
