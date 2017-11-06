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
            availableTests.put(OONITests.HTTP_INVALID_REQUEST_LINE, true);
            availableTests.put(OONITests.HTTP_HEADER_FIELD_MANIPULATION, true);
            availableTests.put(OONITests.NDT, true);
            availableTests.put(OONITests.DASH, true);
        }
        else if (activity == null && a != null){
            activity = a;
        }
        return instance;
    }

    public static NetworkMeasurement configureTest(final Context ctx, final String testName) {
        final NetworkMeasurement currentTest = new NetworkMeasurement(testName);
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

    public static void doNetworkMeasurements(final Context ctx, final String testName, final ArrayList<String> urls) {
        final NetworkMeasurement currentTest = configureTest(ctx, testName);
        TestStorage.addTest(ctx, currentTest);
        runningTests.add(currentTest);
        availableTests.put(testName, false);
        if (activity != null) TestData.getInstance(context, activity).notifyObservers();

        Log.v(TAG, "doNetworkMeasurements " + testName + "...");
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
                            if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                                Log.v(TAG, "running http_invalid_request_line test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomaly_hirl(entry, currentTest);
                                            }
                                        }).run();
                            }
                            else if (testName.compareTo(OONITests.HTTP_HEADER_FIELD_MANIPULATION) == 0) {
                                Log.v(TAG, "running http_header_field_manipulation test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomaly_hhfm(entry, currentTest);
                                            }
                                        })
                                        .run();
                            }
                            else if (testName.compareTo(OONITests.WEB_CONNECTIVITY) == 0) {
                                Log.v(TAG, "running web-connectivity test...");
                                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                                final String max_runtime = preferences.getString("max_runtime", OONITests.MAX_RUNTIME);
                                //This is gonna be handled in test_lists branch
                                if (urls != null && urls.size() > 0) {
                                    for (int i = 0; i < urls.size(); i++)
                                        currentTest.test.add_input(urls.get(i));
                                }
                                else {
                                    currentTest.test.set_options("max_runtime", max_runtime);
                                    currentTest.test.set_input_filepath(null);
                                }
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomaly_wc(entry, currentTest);
                                            }
                                        }).run();
                            }
                            else if (testName.compareTo(OONITests.NDT) == 0) {
                                Log.v(TAG, "running ndt test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomaly_ndt(entry, currentTest);
                                            }
                                        })
                                        .run();
                            }
                            else if (testName.compareTo(OONITests.DASH) == 0) {
                                Log.v(TAG, "running dash test...");
                                currentTest.test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                            @Override
                                            public void callback(String entry) {
                                                setAnomaly_ndt(entry, currentTest);
                                            }
                                        })
                                        .run();
                            } else {
                                throw new UnknownTest(testName);
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
                        availableTests.put(testName, true);
                        if (activity != null) TestData.getInstance(context, activity).notifyObservers(testName);
                        NotificationHandler.notifyTestEnded(ctx, testName);
                        Log.v(TAG, "doNetworkMeasurements " + testName + "... done");
                    }
                }
        );
    }

    public static void setAnomaly_wc(String entry, NetworkMeasurement test){
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
    public static void setAnomaly_hirl(String entry, NetworkMeasurement test){
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
    public static void setAnomaly_hhfm(String entry, NetworkMeasurement test){
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
            System.out.println("JSONException "+ e);
        }
    }

    /*
    on_entry method for ndt and dash test
    if the "failure" key exists and is not null then anomaly will be set to 1 (orange)
    */
    public static void setAnomaly_ndt(String entry, NetworkMeasurement test){
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
