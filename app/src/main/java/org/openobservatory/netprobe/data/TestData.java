package org.openobservatory.netprobe.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Observable;

import org.openobservatory.netprobe.activity.MainActivity;
import org.openobservatory.measurement_kit.jni.sync.OoniSyncApi;
import org.openobservatory.measurement_kit.jni.sync.PortolanSyncApi;
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
                        OoniSyncApi.dnsInjection("8.8.8.1", inputPath, outputPath, logPath, true,
                                nameserver);
                    } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                        OoniSyncApi.httpInvalidRequestLine("http://213.138.109.232/",
                                outputPath, logPath, true, nameserver);
                    } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
                        OoniSyncApi.tcpConnect("80", inputPath,  outputPath, logPath, true,
                                nameserver);
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
}
