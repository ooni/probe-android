package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;


public class ServiceUtil {
    public static void scheduleJob(Context context) {
        System.out.println("SyncService scheduleJob");
        Application app = ((Application)context.getApplicationContext());
        PreferenceManager pm = app.getPreferenceManager();
        ComponentName serviceComponent = new ComponentName(context, RunTestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);

        int networkConstraint = pm.testWifiOnly() ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY;
        builder.setRequiredNetworkType(networkConstraint);

        //TODO options to consider for the future https://github.com/ooni/probe/issues/916#issuecomment-722268148

        //Settings to consider for the future
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(true);
        //builder.setPersisted(true);

        //.setOverrideDeadline(timeTillFutureJob)

        //job will fire after 5 seconds when the network becomes available, or no later than 5 minutes

        //Specify that this job should be delayed by the provided amount of time.
        //builder.setMinimumLatency(5 * 1000); // wait at least

        //https://www.coderzheaven.com/2016/11/22/how-to-create-a-simple-repeating-job-using-jobscheduler-in-android/
        //the job will be executed anyway after 5 minutes waiting
        //java.lang.IllegalArgumentException: Can't call setOverrideDeadline() on a periodic job
        //Set deadline which is the maximum scheduling latency.

        //Without this option it doesn't work at all
        //builder.setOverrideDeadline(1 * 60 * 1000); // maximum delay
        builder.setOverrideDeadline(0); // maximum delay

        //builder.setPersisted(true); //Job scheduled to work after reboot
        //builder.setBackoffCriteria()
        /* Minimum flex for a periodic job, in milliseconds. */
        //http://androidxref.com/8.0.0_r4/xref/frameworks/base/core/java/android/app/job/JobInfo.java#110
        //builder.setPeriodic(30 * 60 * 1000);

        //JobScheduler is specifically designed for inexact timing, so it can combine jobs from multiple apps, to try to reduce power consumption.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
