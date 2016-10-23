package org.openobservatory.netprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Observable;

import org.openobservatory.measurement_kit.OoniTestWrapper;
import org.openobservatory.netprobe.activity.MainActivity;
import org.openobservatory.measurement_kit.sync.OoniSyncApi;
import org.openobservatory.measurement_kit.sync.PortolanSyncApi;
import org.openobservatory.netprobe.model.NetworkMeasurement;
import org.openobservatory.netprobe.model.OONITests;
import org.openobservatory.netprobe.model.PortolanTests;
import org.openobservatory.netprobe.model.UnknownTest;

/**
 * Created by lorenzo on 26/04/16.
 */
public class TestData extends Observable {
    private static final String TAG = "TestData";
    private static TestData instance;
    private static TestStorage ts;

    public static TestData getInstance() {
        if (instance == null) {
            instance = new TestData();
            ts = new TestStorage();
        }
        return instance;
    }

    public static void doNetworkMeasurements(final MainActivity activity, final String testName) {
        final String inputPath = activity.getFilesDir() + "/hosts.txt";

        final NetworkMeasurement currentTest = new NetworkMeasurement(testName);
        final String outputPath = activity.getFilesDir() + "/"  + currentTest.json_file;
        final String logPath = activity.getFilesDir() + "/"  + currentTest.log_file;

        final String geoip_asn = activity.getFilesDir() + "/GeoIPASNum.dat";
        final String geoip_country = activity.getFilesDir() + "/GeoIP.dat";
        final String ca_cert = activity.getFilesDir() + "/cacert.pem";

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final Boolean include_ip = preferences.getBoolean("include_ip", false);
        final Boolean include_asn = preferences.getBoolean("include_asn", false);
        final Boolean include_cc = preferences.getBoolean("include_cc", true);
        final Boolean upload_results = preferences.getBoolean("upload_results", true);
        final String collector_address = preferences.getString("collector_address", "https://measurement-kit-collector.herokuapp.com");

        ts.addTest(activity, currentTest);
        TestData.getInstance().notifyObservers();

        // The app now tries to get DNS from the device. Upon fail, it uses
        // Google DNS resolvers
        String nameserver_ = "8.8.8.8";
        ArrayList<String> nameservers = getDNS();
        if (!nameservers.isEmpty()) {
            for (String s : getDNS()) {
                nameserver_ = s;
                Log.v(TAG, "Adding nameserver: " + s);
                break;
            }
        }
        final String nameserver = nameserver_+":53";

        Log.v(TAG, "doNetworkMeasurements " + testName + "...");

        /*
        Using AsyncTask  may not be the optimal solution since OONI tests could take a long time to complete
        For more info read : http://developer.android.com/reference/android/os/AsyncTask.html
        */
        new AsyncTask<String, String, Boolean>(){
            @Override
            protected Boolean doInBackground(String... params)
            {
                try
                {
                    Log.v(TAG, "running test...");
                    // TODO: query the device for its name server and use it rather than using
                    // google's public name server for the same purpose
                    if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
                        OoniTestWrapper w = new OoniTestWrapper("dns_injection");
                        Log.v(TAG, "running new style dns_injection test...");
                        w.use_logcat();
                        w.set_options("backend", "8.8.8.1");
                        w.set_input_filepath(inputPath);
                        w.set_output_filepath(outputPath);
                        w.set_error_filepath(logPath);
                        w.set_verbosity(7);
                        w.set_options("net/ca_bundle_path", ca_cert);
                        w.set_options("geoip_country_path", geoip_country);
                        w.set_options("geoip_asn_path", geoip_asn);
                        w.set_options("save_real_probe_ip", boolToString(include_ip));
                        w.set_options("save_real_probe_asn", boolToString(include_asn));
                        w.set_options("save_real_probe_cc", boolToString(include_cc));
                        w.set_options("no_collector", boolToString(!upload_results));
                        w.set_options("collector_base_url", collector_address);
                        w.set_options("dns/nameserver", nameserver);
                        w.run();
                    } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                        OoniTestWrapper w = new OoniTestWrapper("http_invalid_request_line");
                        Log.v(TAG, "running new style http_invalid_request_line test...");
                        w.use_logcat();
                        w.set_options("backend", "http://213.138.109.232/");
                        w.set_output_filepath(outputPath);
                        w.set_error_filepath(logPath);
                        w.set_verbosity(7);
                        w.set_options("net/ca_bundle_path", ca_cert);
                        w.set_options("geoip_country_path", geoip_country);
                        w.set_options("geoip_asn_path", geoip_asn);
                        w.set_options("save_real_probe_ip", boolToString(include_ip));
                        w.set_options("save_real_probe_asn", boolToString(include_asn));
                        w.set_options("save_real_probe_cc", boolToString(include_cc));
                        w.set_options("no_collector", boolToString(!upload_results));
                        w.set_options("collector_base_url", collector_address);
                        w.set_options("dns/nameserver", nameserver);
                        w.run();
                    } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
                        // TODO: basically we can pass the test name to the constructor
                        // and then work onto the returned generic object once we are ready
                        Log.v(TAG, "running new style tcp-connect test...");
                        Log.v(TAG, "xx " + ca_cert);
                        OoniTestWrapper w = new OoniTestWrapper("tcp_connect");
                        w.use_logcat();
                        w.set_options("port", "80");
                        w.set_input_filepath(inputPath);
                        w.set_output_filepath(outputPath);
                        w.set_error_filepath(logPath);
                        w.set_verbosity(7);
                        w.set_options("net/ca_bundle_path", ca_cert);
                        w.set_options("geoip_country_path", geoip_country);
                        w.set_options("geoip_asn_path", geoip_asn);
                        w.set_options("save_real_probe_ip", boolToString(include_ip));
                        w.set_options("save_real_probe_asn", boolToString(include_asn));
                        w.set_options("save_real_probe_cc", boolToString(include_cc));
                        w.set_options("no_collector", boolToString(!upload_results));
                        w.set_options("collector_base_url", collector_address);
                        w.set_options("dns/nameserver", nameserver);
                        w.run();
                    } else if (testName.compareTo(PortolanTests.CHECK_PORT) == 0) {
                        PortolanSyncApi.checkPort(true, "130.192.91.211", "81", 4.0, true);
                    } else if (testName.compareTo(PortolanTests.TRACEROUTE) == 0) {
                        PortolanTests.runTraceroute();
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

            protected void onProgressUpdate(String... values)
            {
            }

            protected void onPostExecute(Boolean success) {
                ts.setCompleted(activity, currentTest);
                TestData.getInstance().notifyObservers();
                Log.v(TAG, "doNetworkMeasurements " + testName + "... done");
            }
        }.execute();
    }

    @Override
    public void notifyObservers(Object type) {
        setChanged(); // Set the changed flag to true, otherwise observers won't be notified.
        super.notifyObservers(type);
    }

    private static ArrayList<String> getDNS() {
        ArrayList<String> servers = new ArrayList<String>();
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);

            for (String name : new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4",}) {
                String value = (String) method.invoke(null, name);
                if (value != null && !value.equals("") && !servers.contains(value)) {
                    servers.add(value);
                }
            }
            // Using 4 branches to show which errors may occur
            // We can just catch Exception
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getDNS: error: " + e);
        }
        return servers;
    }

    public static String boolToString(Boolean b) {
        return b ? "1" : "0";
    }
}
