package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;


public class ServiceUtil {
    private static final int id = 100;

    public static void scheduleJob(Context context) {
        System.out.println("SyncService scheduleJob");
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

        //Settings to consider for the future
        //This tells your job to not start unless the user is not using their device and they have not used it for some time.
        //builder.setRequiresDeviceIdle(true); // device should be idle

        //REMOVED SETTINGS DUE TO:
        //java.lang.IllegalArgumentException: Can't call setOverrideDeadline() on a periodic job

        //job will fire after 5 seconds when the network becomes available, or no later than 5 minutes
        //Specify that this job should be delayed by the provided amount of time.
        //builder.setMinimumLatency(5 * 1000); // wait at least
        //the job will be executed anyway after 5 minutes waiting
        //Set deadline which is the maximum scheduling latency.
        //builder.setOverrideDeadline(5 * 60 * 1000); // maximum delay
        /* Minimum flex for a periodic job, in milliseconds. */
        //http://androidxref.com/8.0.0_r4/xref/frameworks/base/core/java/android/app/job/JobInfo.java#110



        //builder.setBackoffCriteria()


        //JobScheduler is specifically designed for inexact timing, so it can combine jobs from multiple apps, to try to reduce power consumption.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void stopJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(id);
    }

}
