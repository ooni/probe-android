package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "alarm-receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Application app = ((Application)context.getApplicationContext());
        PreferenceManager pm = app.getPreferenceManager();
        AbstractSuite websitesSuite = new WebsitesSuite();
        AbstractTest webConnectivity = new WebConnectivity();

        //Abort test in case the user is not connected to WiFi
        if (pm.testWifiOnly() &&
                !ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.WIFI)) {
            Log.d(TAG, "is wifi only and not wifi, aborting");
            NotificationService.notifyTestError(context, websitesSuite);
            return;
        }

        //TODO log stuff in countly
        webConnectivity.setMax_runtime(pm.getMaxRuntimeAutoTest());
        websitesSuite.setTestList(webConnectivity);
        ArrayList<AbstractSuite> testSuites = new ArrayList<>(Arrays.asList(websitesSuite));
        Intent i = RunningActivity.newBackgroundIntent(context, testSuites);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "starting test");

        //Send notification before the test starts
        NotificationService.notifyTestStarted(context, websitesSuite);

        context.startActivity(i);
    }
}