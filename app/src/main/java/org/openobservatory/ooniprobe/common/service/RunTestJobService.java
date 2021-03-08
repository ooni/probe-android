package org.openobservatory.ooniprobe.common.service;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import io.sentry.protocol.App;

public class RunTestJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        //Intent service = new Intent(getApplicationContext(), RunTestService.class);
        Application app = ((Application)getApplicationContext());
        Log.d(TAG, "is started");

        //TEST1 only send notification every hour
        /*
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        NotificationService.setChannel(getApplicationContext(), "RunTestService", app.getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
        NotificationService.sendNotification(getApplicationContext(), "RunTestService", "Should run test", "Time is "+currentTime);
*/
/*
        //TEST2 call the API
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Application app = ((Application)getApplicationContext());
                    ServiceUtil.callCheckInAPI(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
*/
        new JobTask(this, app).execute(params);

        //TODO
        //jobFinished(jobParams, needResheduleBoolean);
        /*
        //TODO-SERVICE-BK log stuff in countly
        PreferenceManager pm = app.getPreferenceManager();
        AbstractSuite websitesSuite = new WebsitesSuite();
        AbstractTest webConnectivity = new WebConnectivity();
        //webConnectivity.setMax_runtime(pm.getMaxRuntimeAutoTest());
        websitesSuite.setTestList(webConnectivity);
        ArrayList<AbstractSuite> testSuites = new ArrayList<>(Arrays.asList(websitesSuite));

        Intent serviceIntent = new Intent(getApplicationContext(), RunTestService.class);
        serviceIntent.putExtra("testSuites", testSuites);

        ContextCompat.startForegroundService(this, serviceIntent);
        //getApplicationContext().startService(serviceIntent);
        //ServiceUtil.scheduleJob(getApplicationContext()); // reschedule the job
         */
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("SyncService onStopJob");
        //jobFinished(params, true);
        return true;
    }

    private static class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;
        private final Application app;

        public JobTask(JobService jobService, Application app) {
            this.jobService = jobService;
            this.app = app;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            System.out.println("SyncService doInBackground");
            ServiceUtil.callCheckInAPI(app);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            System.out.println("SyncService onPostExecute");
            /*
             * In cases where the background work fails (it could be because of an HTTP request that failed, for example),
             * you may want to retry before the next expected execution of the job.
             * That's when you pass true for jobFinished's needsReschedule.
             */
            jobService.jobFinished(jobParameters, false);
        }
    }

}
