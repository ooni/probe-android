package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import androidx.core.content.ContextCompat;

import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.domain.GenerateAutoRunServiceSuite;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import javax.inject.Inject;


public class ServiceUtil {
    private static final int id = 100;

    static Dependencies d = new Dependencies();

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
        JobScheduler jobScheduler = ContextCompat.getSystemService(context, JobScheduler.class);;
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void stopJob(Context context) {
        JobScheduler jobScheduler = ContextCompat.getSystemService(context, JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.cancel(id);
        }
    }

    public static void callCheckInAPI(Application app) {
        app.getServiceComponent().inject(d);

        BatteryManager batteryManager = (BatteryManager) app.getSystemService(Context.BATTERY_SERVICE);
        boolean workingOnWifi = ReachabilityManager.getNetworkType(app).equals(ReachabilityManager.WIFI);
        boolean phoneCharging = false;
        String[] categories = d.preferenceManager.getEnabledCategoryArr().toArray(new String[0]);
        boolean isVPNInUse = ReachabilityManager.isVPNinUse(app);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            phoneCharging = batteryManager.isCharging();
        }

        if (!d.generateAutoRunServiceSuite.shouldStart(workingOnWifi,phoneCharging, isVPNInUse)) {
            return;
        }

        OONICheckInConfig config = new OONICheckInConfig(
                BuildConfig.SOFTWARE_NAME,
                BuildConfig.VERSION_NAME,
                workingOnWifi,
                phoneCharging,
                categories
               );

        AbstractSuite suite = d.generateAutoRunServiceSuite.generate(config);

        if (suite != null) {
            Intent serviceIntent = new Intent(app, RunTestService.class);
            serviceIntent.putExtra("testSuites", suite.asArray());
            serviceIntent.putExtra("storeDB", false);
            ContextCompat.startForegroundService(app, serviceIntent);
        }

    }

    public static class Dependencies {
        @Inject
        GenerateAutoRunServiceSuite generateAutoRunServiceSuite;

        @Inject
        PreferenceManager preferenceManager;
    }
}
