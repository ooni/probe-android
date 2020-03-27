package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(DEBUG_TAG);
        //NotificationService.sendNotification(context, "", null);
        /*
        Two  strategies
        - Start running activity
        - Start silent test
        This second approach ahs two problems:
        - TestAsyncTaskImpl extends TestAsyncTask<RunningActivity>  so I need to refactor all the code to run tests
        - If a test is running and the user opens the app he could potentially run another test and mess up
        */

        WebsitesSuite suite = new WebsitesSuite();
        Intent i = RunningActivity.newIntent(context, suite);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        /*
        String[] testArray = {
                OONITests.WEB_CONNECTIVITY,
                OONITests.HTTP_HEADER_FIELD_MANIPULATION,
                OONITests.HTTP_INVALID_REQUEST_LINE,
                OONITests.WHATSAPP,
                OONITests.TELEGRAM,
                OONITests.FACEBOOK_MESSENGER
        };
        for (int i=0; i<testArray.length; i++)
        {
            String testName = testArray[i];
            Answers.getInstance().logCustom(new CustomEvent("Automated testing run")
                    .putCustomAttribute("status", "started"));
        }
        NotificationHandler.sendNotification(context, context.getString(R.string.local_notifications_text));
        TestData.getInstance(context, null);
        for (int i=0; i<testArray.length; i++)
        {
            String testName = testArray[i];
            TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, testName, true));
        }
        */
    }
}