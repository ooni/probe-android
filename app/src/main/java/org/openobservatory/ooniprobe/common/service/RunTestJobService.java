package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class RunTestJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), RunTestService.class);
        getApplicationContext().startService(service);
        ServiceUtil.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
