package org.openobservatory.ooniprobe.common.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Arrays;

public class RunTestJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        //Intent service = new Intent(getApplicationContext(), RunTestService.class);
        Log.d(TAG, "is started");
        //TODO-SERVICE-BK log stuff in countly

        Application app = ((Application)getApplicationContext());
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
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
