package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;

import org.openobservatory.ooniprobe.common.Application;

import java.lang.ref.WeakReference;

public class RunTestJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Application app = ((Application)getApplicationContext());
        new JobTask(this, app).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private static class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final WeakReference<JobService> jobServiceRef;
        private final Application app;

        public JobTask(JobService jobService, Application app) {
            this.jobServiceRef = new WeakReference<>(jobService);
            this.app = app;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ServiceUtil.callCheckInAPI(app);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            /*  //TODO
             * In cases where the background work fails (it could be because of an HTTP request that failed, for example),
             * you may want to retry before the next expected execution of the job.
             * That's when you pass true for jobFinished's needsReschedule.
             */
            jobServiceRef.get().jobFinished(jobParameters, false);
        }
    }

}
