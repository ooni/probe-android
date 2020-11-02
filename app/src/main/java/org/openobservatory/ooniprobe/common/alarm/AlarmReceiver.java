package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.openobservatory.engine.Engine;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
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
    private static final String TAG = "alarm-receiver";

    //TODO-SERVICE MOVE this to RunTestJobService
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

        if (!ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            Log.d(TAG, "no internet available");
            NotificationService.notifyTestError(context, websitesSuite);
            return;
        }

        //TODO-SERVICE log stuff in countly
        webConnectivity.setMax_runtime(pm.getMaxRuntimeAutoTest());
        websitesSuite.setTestList(webConnectivity);

        ArrayList<AbstractSuite> testSuites = new ArrayList<>(Arrays.asList(websitesSuite));
        //Intent i = RunningActivity.newBackgroundIntent(context, testSuites);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "starting test");

        //Send notification before the test starts
        NotificationService.notifyTestStarted(context, websitesSuite);
        //runWebConnectivity(context, app);
        //context.startActivity(i);
        //TestAsyncTask task = (TestAsyncTask) new TestAsyncTask(app, websitesSuite.getResult()).execute(websitesSuite.getTestList(pm));
        //Problem, the test result is shown as error until the test completes.
    }

    //TODO-SERVICE used for early testing. TO REMOVE
    public void runWebConnectivity(Context c, Application a) {
        boolean submitted = false;
        String report_id_1 = "r1";
        String report_id_2 = "r2";
        OONIMKTask task = null;
        Settings settings = new Settings(c, a.getPreferenceManager());
        Gson gson = a.getGson();
        settings.name = "WebConnectivity";
        settings.inputs = Collections.singletonList("http://mail.google.com");
        settings.options.max_runtime = 10;
        settings.annotations.origin = TAG;
        settings.options.no_collector = false;
        try {
            task = Engine.startExperimentTask(settings.toExperimentSettings(gson, c));
        } catch (Exception exc) {
        }
        while (!task.isDone()){
            try {
                String json = task.waitForNextEvent();
                Log.d(TAG, json);
                EventResult event = gson.fromJson(json, EventResult.class);
                switch (event.key) {
                    case "status.report_create":
                        report_id_1 = event.value.report_id;
                        break;
                    case "measurement":
                        JsonResult jr = gson.fromJson(event.value.json_str, JsonResult.class);
                        report_id_2 = jr.report_id;
                        break;
                    case "failure.report_create":
                    case "failure.measurement_submission":
                    case "failure.startup":
                        break;
                    case "status.measurement_submission":
                        submitted = true;
                        break;
                    default:
                        break;
                }
            } catch (Exception ignored) {
            }
        }
    }

}