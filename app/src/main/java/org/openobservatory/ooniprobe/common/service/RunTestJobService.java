package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import org.openobservatory.ooniprobe.common.Application;

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
