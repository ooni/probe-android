// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.app;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import io.github.measurement_kit.jni.sync.OoniSyncApi;
import io.github.measurement_kit.jni.sync.PortolanSyncApi;

public class SyncRunnerService extends IntentService {

    public SyncRunnerService() {
        super("runner-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String testName = intent.getAction();
        Log.v(TAG, "onHandleIntent " + testName + "...");
        String path = getFilesDir() + "/hosts.txt";

        Log.v(TAG, "running test...");
        if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
            OoniSyncApi.dnsInjection("8.8.8.1", path, true, "");
        } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
            OoniSyncApi.httpInvalidRequestLine("http://213.138.109.232/", true, "");
        } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
            OoniSyncApi.tcpConnect("80", path, true, "");
        } else if (testName.compareTo(PortolanTests.CHECK_PORT) == 0) {
            PortolanSyncApi.checkPort(true, "130.192.91.211", "81", 4.0, true);
        } else if (testName.compareTo(PortolanTests.TRACEROUTE) == 0) {
            PortolanTests.runTraceroute();
        } else {
            throw new UnknownTest(testName);
        }
        Log.v(TAG, "running test... done");

        Log.v(TAG, "report back...");
        intent = new Intent(testName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.v(TAG, "report back... done");

        Log.v(TAG, "onHandleIntent " + testName + "... done");
    }

    private static final String TAG = "runner-service";
}
