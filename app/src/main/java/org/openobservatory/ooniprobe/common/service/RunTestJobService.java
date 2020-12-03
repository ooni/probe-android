package org.openobservatory.ooniprobe.common.service;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
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

public class RunTestJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        //Intent service = new Intent(getApplicationContext(), RunTestService.class);
        Application app = ((Application)getApplicationContext());

        //TEST1 only send notification every hour
        Log.d(TAG, "is started");
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        NotificationService.setChannel(getApplicationContext(), "RunTestService", app.getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
        NotificationService.sendNotification(getApplicationContext(), "Should run test", "Time is "+currentTime);

        //TODO-TEST2 call the API


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
        return true;
    }
}
