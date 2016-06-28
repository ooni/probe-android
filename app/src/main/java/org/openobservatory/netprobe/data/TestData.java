package org.openobservatory.netprobe.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    //public static ArrayList<NetworkMeasurement> mNetworkMeasurementsRunning = new ArrayList<>();
    //public static ArrayList<NetworkMeasurement> mNetworkMeasurementsFinished = new ArrayList<>();

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
//        String ts = tsLong.toString();
//        final String filename = "/text-"+ ts +".txt";
//        final String logPath = activity.getFilesDir() + filename;
//        final String outputPath = activity.getFilesDir() + "/test-"+ ts +".json";

        final NetworkMeasurement currentTest = new NetworkMeasurement(testName);
        final String outputPath = activity.getFilesDir() + "/"  + currentTest.json_file;
        final String logPath = activity.getFilesDir() + "/"  + currentTest.log_file;

        //mNetworkMeasurementsRunning.add(currentTest);
        ts.addTest(activity, currentTest);
        TestData.getInstance().notifyObservers();

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
                    //progress = ProgressDialog.show(activity, "Testing", "running tcp-connect test", false);
                    Log.v(TAG, "running test...");
                    // TODO: query the device for its name server and use it rather than using
                    // google's public name server for the same purpose
                    if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
                        OoniSyncApi.dnsInjection("8.8.8.1", inputPath, outputPath, logPath, true,
                                "8.8.8.8:53");
                    } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                        OoniSyncApi.httpInvalidRequestLine("http://213.138.109.232/",
                                outputPath, logPath, true, "8.8.8.8:53");
                    } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
                        OoniSyncApi.tcpConnect("80", inputPath,  outputPath, logPath, true,
                                "8.8.8.8:53");
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
                //mNetworkMeasurementsRunning.remove(currentTest);
                ts.setCompleted(activity, currentTest);
                //mNetworkMeasurementsFinished.add(currentTest);
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
}
