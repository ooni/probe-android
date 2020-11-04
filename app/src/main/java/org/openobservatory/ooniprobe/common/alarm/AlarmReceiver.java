package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.openobservatory.engine.Engine;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SyncService alarm received");
        ServiceUtil.scheduleJob(context);
        /*
        Application app = ((Application)context.getApplicationContext());
        PreferenceManager pm = app.getPreferenceManager();
        AbstractSuite websitesSuite = new WebsitesSuite();
        AbstractTest webConnectivity = new WebConnectivity();

        //Abort test in case the user is not connected to WiFi
        if (pm.testWifiOnly() &&
                !ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.WIFI)) {
            Log.d(TAG, "is wifi only and not wifi, aborting");
            NotificationService.notifyTestError(context, websitesSuite);
            //return;
        }

        if (!ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            Log.d(TAG, "no internet available");
            NotificationService.notifyTestError(context, websitesSuite);
            //return;
        }

        webConnectivity.setMax_runtime(pm.getMaxRuntimeAutoTest());
        websitesSuite.setTestList(webConnectivity);
        ArrayList<AbstractSuite> testSuites = new ArrayList<>(Arrays.asList(websitesSuite));
        //Intent i = RunningActivity.newBackgroundIntent(context, testSuites);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "starting test");

        Intent serviceIntent = new Intent(context, RunTestService.class);
        serviceIntent.putExtra("testSuites", testSuites);
        ContextCompat.startForegroundService(context, serviceIntent);
         */
    }

}