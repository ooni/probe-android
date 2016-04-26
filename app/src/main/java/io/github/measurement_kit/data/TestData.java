package io.github.measurement_kit.data;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import io.github.measurement_kit.activity.MainActivity;
import io.github.measurement_kit.jni.sync.OoniSyncApi;
import io.github.measurement_kit.jni.sync.PortolanSyncApi;
import io.github.measurement_kit.model.NetworkMeasurement;
import io.github.measurement_kit.model.OONITests;
import io.github.measurement_kit.model.PortolanTests;
import io.github.measurement_kit.model.UnknownTest;

/**
 * Created by lorenzo on 26/04/16.
 */
public class TestData extends Observable {
    private static final String TAG = "TestData";

    public ArrayList<NetworkMeasurement> mNetworkMeasurementsRunning = new ArrayList<>();
    public ArrayList<NetworkMeasurement> mNetworkMeasurementsFinished = new ArrayList<>();
    public ArrayList<NetworkMeasurement> mNetworkMeasurements = new ArrayList<>();

    private static TestData instance;

    public static TestData getInstance() {
        if (instance == null) {
            instance = new TestData();
        }
        return instance;
    }

    public void doNetworkMeasurements(final MainActivity activity, final String testName) {
        final NetworkMeasurement currentTest = new NetworkMeasurement();
        currentTest.testName = testName;
        currentTest.finished = false;
        mNetworkMeasurementsRunning.add(currentTest);
        Log.v(TAG, "doNetworkMeasurements " + testName + "...");
        final String inputPath = activity.getFilesDir() + "/hosts.txt";
        final String outputPath = activity.getFilesDir() + "/last-report.yml";
        final String logPath = activity.getFilesDir() + "/last-logs.txt";

        new AsyncTask<String, String, Boolean>(){
            @Override
            protected Boolean doInBackground(String... params)
            {
                try
                {
                    //progress = ProgressDialog.show(activity, "Testing", "running tcp-connect test", false);
                    Log.v(TAG, "running test...");
                    if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
                        OoniSyncApi.dnsInjection("8.8.8.1", inputPath, outputPath, logPath, true);
                    } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
                        OoniSyncApi.httpInvalidRequestLine("http://213.138.109.232/",
                                outputPath, logPath, true);
                    } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
                        OoniSyncApi.tcpConnect("80", inputPath,  outputPath, logPath, true);
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
                TestData.getInstance().notifyObservers();
                mNetworkMeasurementsRunning.remove(currentTest);
                currentTest.finished = true;
                mNetworkMeasurementsFinished.add(currentTest);
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
