package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.engine.Engine;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementJsonCallback;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ExceptionManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.api.CheckIn;
import org.openobservatory.ooniprobe.model.api.UrlList;
import org.openobservatory.ooniprobe.model.database.Url;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Response;


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

    public static void callAPITest(Application app){
        PreferenceManager pm = app.getPreferenceManager();
        System.out.println("callAPITest " );
        try {
            Response <CheckIn> response = app.getTestClient().checkIn(
                    pm.testChargingOnly(),
                    pm.testWifiOnly(),
                    "android",
                    "string",
                    "string",
                    "string",
                    "string",
                    "{ \\\"category_codes:\\\": \\\"string\\\" }").execute();
            if (response.isSuccessful() && response.body() != null && response.body().tests != null) {
                ArrayList<String> inputs = new ArrayList<>();
                System.out.println("callAPITest " + response.body().tests.web_connectivity.urls.size());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                //NotificationService.setChannel(app, "RunTestService", app.getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
                //NotificationService.sendNotification(app, "RunTestService", "Should run test", "Time is "+currentTime + "Url size " + response.body().tests.web_connectivity.urls.size());

                //for (Url url : response.body().tests.web_connectivity.urls)
                //    inputs.add(Url.checkExistingUrl(url.url, url.category_code, url.country_code).url);
                /*currentTest.setInputs(inputs);
                if (currentTest.getMax_runtime() == null)
                    currentTest.setMax_runtime(app.getPreferenceManager().getMaxRuntime());
                publishProgress(URL);
                 */
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("callAPITest e " + e);

            ExceptionManager.logException(e);
        }
    }

}
