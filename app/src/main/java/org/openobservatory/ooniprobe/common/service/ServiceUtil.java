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

        // require unmetered network
        if (pm.testWifiOnly()) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        }
        else
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Settings to consider for the future
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        //builder.setPersisted(true);

        //.setOverrideDeadline(timeTillFutureJob)

        //job will fire after 5 seconds when the network becomes available, or no later than 5 minutes

        //Specify that this job should be delayed by the provided amount of time.
        //builder.setMinimumLatency(5 * 1000); // wait at least

        //the job will be executed anyway after 5 minutes waiting
        //java.lang.IllegalArgumentException: Can't call setOverrideDeadline() on a periodic job
        //Set deadline which is the maximum scheduling latency.
        //builder.setOverrideDeadline(5 * 60 * 1000); // maximum delay

        builder.setPersisted(true); //Job scheduled to work after reboot

        /* Minimum flex for a periodic job, in milliseconds. */
        //http://androidxref.com/8.0.0_r4/xref/frameworks/base/core/java/android/app/job/JobInfo.java#110
        builder.setPeriodic(30 * 60 * 1000);

        //JobScheduler is specifically designed for inexact timing, so it can combine jobs from multiple apps, to try to reduce power consumption.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
