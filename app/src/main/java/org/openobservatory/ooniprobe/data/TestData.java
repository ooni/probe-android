package org.openobservatory.ooniprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.android.DnsUtils;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.*;
import org.openobservatory.measurement_kit.swig.OoniTestWrapper;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import org.openobservatory.ooniprobe.model.UnknownTest;
import org.openobservatory.ooniprobe.utils.Notifications;

public class TestData extends Observable {
    private static final String TAG = "TestData";
    private static TestData instance;
    public static ArrayList<NetworkMeasurement> runningTests;
    public static ArrayList<NetworkMeasurement> finishedTests;
    public static LinkedHashMap<String, Boolean> availableTests;
    public static MainActivity activity;

    public static TestData getInstance(final MainActivity a) {
        if (instance == null) {
            activity = a;
            instance = new TestData();
            runningTests = new ArrayList<>();
            finishedTests = TestStorage.loadTests(activity);
            availableTests = new LinkedHashMap<>();
            availableTests.put(OONITests.WEB_CONNECTIVITY, true);
            availableTests.put(OONITests.HTTP_INVALID_REQUEST_LINE, true);
            availableTests.put(OONITests.NDT_TEST, true);
        }
        return instance;
    }

    public static void doNetworkMeasurements(final Context ctx, final String testName) {
        final String inputPath = ctx.getFilesDir() + "/hosts.txt";
        final String inputUrlsPath = ctx.getFilesDir() + "/global.txt";

        final NetworkMeasurement currentTest = new NetworkMeasurement(testName);
        final String outputPath = ctx.getFilesDir() + "/"  + currentTest.json_file;
        final String logPath = ctx.getFilesDir() + "/"  + currentTest.log_file;

        final String geoip_asn = ctx.getFilesDir() + "/GeoIPASNum.dat";
        final String geoip_country = ctx.getFilesDir() + "/GeoIP.dat";

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        final Boolean include_ip = preferences.getBoolean("include_ip", false);
        final Boolean include_asn = preferences.getBoolean("include_asn", false);
        final Boolean include_cc = preferences.getBoolean("include_cc", true);
        final Boolean upload_results = preferences.getBoolean("upload_results", true);
        final String collector_address = preferences.getString("collector_address", "https://b.collector.ooni.io");
        final String max_runtime = preferences.getString("max_runtime", "90");

        TestStorage.addTest(ctx, currentTest);
        runningTests.add(currentTest);
        availableTests.put(testName, false);
        if (activity != null) TestData.getInstance(activity).notifyObservers();

        final String nameserver = DnsUtils.get_device_dns();
        Log.v(TAG, "nameserver: " + nameserver);

        Log.v(TAG, "doNetworkMeasurements " + testName + "...");

        /*
        Using AsyncTask  may not be the optimal solution since OONI tests could take a long time to complete
        For more info read: http://developer.android.com/reference/android/os/AsyncTask.html
        */
        new AsyncTask<String, String, Boolean>(){
            @Override
            protected Boolean doInBackground(String... params)
            {
                try
                {
                    Log.v(TAG, "running test...");
                    if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
                        /*
                         * TODO: add high level class for this test.
                         */
                        OoniTestWrapper w = new OoniTestWrapper("dns_injection");
                        Log.v(TAG, "running dns_injection test...");
                        w.use_logcat();
                        w.set_options("backend", "8.8.8.1");
                        w.set_input_filepath(inputPath);
                        w.set_output_filepath(outputPath);
                        w.set_error_filepath(logPath);
                        w.set_verbosity(LogSeverity.INFO);
                        w.set_options("dns/nameserver", nameserver);
                        w.set_options("geoip_country_path", geoip_country);
                        w.set_options("geoip_asn_path", geoip_asn);
                        w.set_options("save_real_probe_ip", boolToString(include_ip));
                        w.set_options("save_real_probe_asn", boolToString(include_asn));
                        w.set_options("save_real_probe_cc", boolToString(include_cc));
                        w.set_options("no_collector", boolToString(!upload_results));
                        w.set_options("collector_base_url", collector_address);
                        w.run();
                    } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                        Log.v(TAG, "running http_invalid_request_line test...");
                        new HttpInvalidRequestLineTest()
                            .use_logcat()
                            .set_options("backend", "http://213.138.109.232/")
                            .set_output_filepath(outputPath)
                            .set_error_filepath(logPath)
                            .set_verbosity(LogSeverity.INFO)
                            .set_options("dns/nameserver", nameserver)
                            .set_options("geoip_country_path", geoip_country)
                            .set_options("geoip_asn_path", geoip_asn)
                            .set_options("save_real_probe_ip", boolToString(include_ip))
                            .set_options("save_real_probe_asn", boolToString(include_asn))
                            .set_options("save_real_probe_cc", boolToString(include_cc))
                            .set_options("no_collector", boolToString(!upload_results))
                            .set_options("collector_base_url", collector_address)
                            .set_options("software_name", "ooniprobe-android")
                            .set_options("software_version", BuildConfig.VERSION_NAME)
                            .on_progress(new org.openobservatory.measurement_kit.nettests.ProgressCallback() {
                            @Override
                            public void callback(double percent, String msg) {
                                currentTest.progress = (int)(percent*100);
                                if (activity != null){
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TestData.getInstance(activity).notifyObservers();
                                        }
                                    });
                                }
                                }
                            })
                            .on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                @Override
                                public void callback(String entry) {
                                    setAnomaly(entry, currentTest);
                                }
                            })
                            .run();
                    } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
                        Log.v(TAG, "running tcp-connect test...");
                        OoniTestWrapper w = new OoniTestWrapper("tcp_connect");
                        w.use_logcat();
                        w.set_input_filepath(inputPath);
                        w.set_output_filepath(outputPath);
                        w.set_error_filepath(logPath);
                        w.set_verbosity(LogSeverity.INFO);
                        w.set_options("port", "80");
                        w.set_options("dns/nameserver", nameserver);
                        w.set_options("geoip_country_path", geoip_country);
                        w.set_options("geoip_asn_path", geoip_asn);
                        w.set_options("save_real_probe_ip", boolToString(include_ip));
                        w.set_options("save_real_probe_asn", boolToString(include_asn));
                        w.set_options("save_real_probe_cc", boolToString(include_cc));
                        w.set_options("no_collector", boolToString(!upload_results));
                        w.set_options("collector_base_url", collector_address);
                        w.run();
                    }
                    else if (testName.compareTo(OONITests.WEB_CONNECTIVITY) == 0) {
                        Log.v(TAG, "running web-connectivity test...");
                        new WebConnectivityTest()
                            .use_logcat()
                            .set_input_filepath(inputUrlsPath)
                            .set_output_filepath(outputPath)
                            .set_error_filepath(logPath)
                            .set_verbosity(LogSeverity.INFO)
                            .set_options("backend", "https://b.web-connectivity.th.ooni.io")
                            /*
                             * XXX nameserver is the nameserver to be used for
                             * the DNS phase of web-connectivity only while
                             * dns/nameserver is the one used for all the other
                             * DNS operations. Do we need to have both?
                             */
                            .set_options("dns/nameserver", nameserver)
                            .set_options("nameserver", nameserver)
                            .set_options("geoip_country_path", geoip_country)
                            .set_options("geoip_asn_path", geoip_asn)
                            .set_options("save_real_probe_ip", boolToString(include_ip))
                            .set_options("save_real_probe_asn", boolToString(include_asn))
                            .set_options("save_real_probe_cc", boolToString(include_cc))
                            .set_options("no_collector", boolToString(!upload_results))
                            .set_options("collector_base_url", collector_address)
                            .set_options("max_runtime", max_runtime)
                            .set_options("software_name", "ooniprobe-android")
                            .set_options("software_version", BuildConfig.VERSION_NAME)
                            .on_progress(new org.openobservatory.measurement_kit.nettests.ProgressCallback() {
                                @Override
                                public void callback(double percent, String msg) {
                                    currentTest.progress = (int)(percent*100);
                                    if (activity != null){
                                        activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                TestData.getInstance(activity).notifyObservers();
                                            }
                                    });
                                    }
                                }
                            })
                            .on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                @Override
                                public void callback(String entry) {
                                    setAnomaly(entry, currentTest);
                                }
                            })
                            .run();
                    }
                    else if (testName.compareTo(OONITests.NDT_TEST) == 0) {
                        Log.v(TAG, "running ndt test...");
                        new NdtTest()
                            .use_logcat()
                            .set_input_filepath(inputPath)
                            .set_output_filepath(outputPath)
                            .set_error_filepath(logPath)
                            .set_verbosity(LogSeverity.INFO)
                            .set_options("dns/nameserver", nameserver)
                            .set_options("geoip_country_path", geoip_country)
                            .set_options("geoip_asn_path", geoip_asn)
                            .set_options("save_real_probe_ip", boolToString(include_ip))
                            .set_options("save_real_probe_asn", boolToString(include_asn))
                            .set_options("save_real_probe_cc", boolToString(include_cc))
                            .set_options("no_collector", boolToString(!upload_results))
                            .set_options("collector_base_url", collector_address)
                            .set_options("software_name", "ooniprobe-android")
                            .set_options("software_version", BuildConfig.VERSION_NAME)
                            .on_progress(new org.openobservatory.measurement_kit.nettests.ProgressCallback() {
                            @Override
                            public void callback(double percent, String msg) {
                                currentTest.progress = (int)(percent*100);
                                if (activity != null){
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TestData.getInstance(activity).notifyObservers();
                                        }
                                    });
                                }
                            }
                            })
                            .on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
                                @Override
                                public void callback(String entry) {
                                    setAnomaly(entry, currentTest);
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
                currentTest.completed = true;
                runningTests.remove(currentTest);
                finishedTests.add(currentTest);
                availableTests.put(testName, true);
                if (activity != null) TestData.getInstance(activity).notifyObservers();
                Notifications.notifyTestEnded(ctx, testName);
                Log.v(TAG, "doNetworkMeasurements " + testName + "... done");
            }
        }.execute();
    }

    public static void setAnomaly(String entry, NetworkMeasurement test){
        try {
            int anomaly = 0;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject blocking = jsonObj.getJSONObject("test_keys");
            Object object = blocking.get("blocking");
            if(object instanceof String)
                anomaly = 2;
            else if(object instanceof Boolean)
                anomaly = 0;
            else
                anomaly = 1;
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                TestStorage.setAnomaly(activity, test.test_id, anomaly);
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
        for (int j = 0; j < TestData.getInstance(activity).runningTests.size(); j++) {
            NetworkMeasurement current = TestData.getInstance(activity).runningTests.get(j);
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
