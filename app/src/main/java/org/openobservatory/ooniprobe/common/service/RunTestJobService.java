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
import org.openobservatory.ooniprobe.common.CountlyManager;
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
        CountlyManager.recordEvent("AutomaticTest_started");
        Application app = ((Application)getApplicationContext());
        new JobTask(this, app).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
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
            ServiceUtil.callCheckInAPI(app);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            /*  //TODO
             * In cases where the background work fails (it could be because of an HTTP request that failed, for example),
             * you may want to retry before the next expected execution of the job.
             * That's when you pass true for jobFinished's needsReschedule.
             */
            jobService.jobFinished(jobParameters, false);
        }
    }

}
