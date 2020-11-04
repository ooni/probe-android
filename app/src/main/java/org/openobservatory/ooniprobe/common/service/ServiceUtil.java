package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;


public class ServiceUtil {
    public static void scheduleJob(Context context) {
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

        //Specify that this job should be delayed by the provided amount of time.
        //builder.setMinimumLatency(1 * 1000); // wait at least

        //Set deadline which is the maximum scheduling latency.
        //builder.setOverrideDeadline(3 * 1000); // maximum delay

        //JobScheduler is specifically designed for inexact timing, so it can combine jobs from multiple apps, to try to reduce power consumption.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
