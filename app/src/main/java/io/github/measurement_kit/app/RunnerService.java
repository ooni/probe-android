// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.app;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import io.github.measurement_kit.common.Async;
import io.github.measurement_kit.common.NetTest;
import io.github.measurement_kit.ooni.DNSInjection;
import io.github.measurement_kit.ooni.HTTPInvalidRequestLine;
import io.github.measurement_kit.ooni.TCPConnect;

public class RunnerService extends IntentService {
    // So, currently I'm using intent-service for simplicity even though
    // this means that tests cannot run in parallel. Will lift this limitation.

    public RunnerService() {
        super("runner-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String testName = intent.getAction();
        Log.v(TAG, "onHandleIntent: " + testName);

        Log.v(TAG, "creating test...");
        NetTest test;
        if (testName.compareTo(OONITests.DNS_INJECTION) == 0) {
            test = new DNSInjection(getFilesDir() + "/hosts.txt", "8.8.8.8");
        } else if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0) {
            test = new HTTPInvalidRequestLine("http://www.google.com/");
        } else if (testName.compareTo(OONITests.TCP_CONNECT) == 0) {
            test = new TCPConnect(getFilesDir() + "/hosts.txt", "80");
        } else {
            throw new UnknownTest(testName);
        }
        Log.v(TAG, "creating test... done");

        Log.v(TAG, "start test...");
        test.setVerbose(true);
        Async.getInstance().runTest(
            test, new RunningTest(this, testName)
        );
        Log.v(TAG, "start test... done");

        Log.v(TAG, "run loop...");
        while (mAgain) {
            Async.getInstance().loopOnce();
        }
        Log.v(TAG, "run loop... done");

        Log.v(TAG, "report back...");
        intent = new Intent(testName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.v(TAG, "report back... done");
    }

    private void stopLoop() {
        mAgain = false;
    }

    private class RunningTest implements Runnable {
        public RunningTest(RunnerService service, String testName) {
            mService = service;
            mTestName = testName;
        }

        @Override
        public void run() {
            Log.v(TAG, "test complete: " + mTestName);
            mService.stopLoop();
        }

        private RunnerService mService;
        private String mTestName;
    }

    private static final String TAG = "runner-service";
    private boolean mAgain = true;
}
