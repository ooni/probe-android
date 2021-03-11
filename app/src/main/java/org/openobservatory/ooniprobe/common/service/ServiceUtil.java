package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import org.openobservatory.engine.Engine;
import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.CountlyManager;
import org.openobservatory.ooniprobe.common.ExceptionManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ServiceUtil {
    private static final int id = 100;

    public static void scheduleJob(Context context) {
        CountlyManager.recordEvent("AutomaticTest_activated");
        Application app = ((Application)context.getApplicationContext());
        PreferenceManager pm = app.getPreferenceManager();
        ComponentName serviceComponent = new ComponentName(context, RunTestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(id, serviceComponent);

        //Options explication https://www.coderzheaven.com/2016/11/22/how-to-create-a-simple-repeating-job-using-jobscheduler-in-android/
        int networkConstraint = pm.testWifiOnly() ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY;
        builder.setRequiredNetworkType(networkConstraint);
        builder.setRequiresCharging(pm.testChargingOnly());

        /*
        * Specify that this job should recur with the provided interval, not more than once per period.
        * You have no control over when within this interval
        * this job will be executed, only the guarantee that it will be executed at most once within this interval.
        */
        builder.setPeriodic(60 * 60 * 1000);
        builder.setPersisted(true); //Job scheduled to work after reboot
        
        //JobScheduler is specifically designed for inexact timing, so it can combine jobs from multiple apps, to try to reduce power consumption.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void stopJob(Context context) {
        CountlyManager.recordEvent("AutomaticTest_deactivated");
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(id);
    }

    public static void callCheckInAPI(Application app) {
        try {
            OONISession session = Engine.newSession(Engine.getDefaultSessionConfig(
                    app, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME, new LoggerArray()));
            OONIContext ooniContext = session.newContextWithTimeout(30);
            session.maybeUpdateResources(ooniContext);
			OONICheckInConfig config = new OONICheckInConfig(
					BuildConfig.SOFTWARE_NAME,
					BuildConfig.VERSION_NAME,
                    ReachabilityManager.isOnWifi(app),
                    ReachabilityManager.isCharging(app),
					app.getPreferenceManager().getEnabledCategoryArr().toArray(new String[0]));
			OONICheckInResults results = session.checkIn(ooniContext, config);
            if (results.webConnectivity != null) {
                ArrayList<String> inputs = new ArrayList<>();
                for (OONIURLInfo url : results.webConnectivity.urls){
                    inputs.add(Url.checkExistingUrl(url.url, url.category_code, url.country_code).url);
                }
                AbstractSuite suite = AbstractSuite.getSuite(app, "web_connectivity",
                        inputs,"autorun");
                if (suite != null) {
                    Intent serviceIntent = new Intent(app, RunTestService.class);
                    serviceIntent.putExtra("testSuites", suite.asArray());
                    ContextCompat.startForegroundService(app, serviceIntent);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            ExceptionManager.logException(e);
        }
    }

}
