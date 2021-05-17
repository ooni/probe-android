package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.domain.StartRunTestService;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import javax.inject.Inject;


public class ServiceUtil {
    private static final int id = 100;

    static Dependencies d = new Dependencies();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context) {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void stopJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(id);
    }

    public static void callCheckInAPI(Application app) {
        app.getServiceComponent().inject(d);

        BatteryManager batteryManager = (BatteryManager) app.getSystemService(Context.BATTERY_SERVICE);
        Boolean workingOnWifi = ReachabilityManager.getNetworkType(app).equals(ReachabilityManager.WIFI);
        Boolean phoneCharging = batteryManager.isCharging();

        if (!d.startRunTestService.shouldStart(workingOnWifi, phoneCharging)) {
            return;
        }

        try {
            AbstractSuite suite = AbstractSuite.getSuite(
                    app,
                    "web_connectivity",
                    d.startRunTestService.getUrls(workingOnWifi, phoneCharging),
                    "autorun"
            );

            if (suite != null) {
                d.startRunTestService.startedRun();
                Intent serviceIntent = new Intent(app, RunTestService.class);
                serviceIntent.putExtra("testSuites", suite.asArray());
                serviceIntent.putExtra("storeDB", false);
                ContextCompat.startForegroundService(app, serviceIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }

    public static class Dependencies {
        @Inject StartRunTestService startRunTestService;
    }
}
