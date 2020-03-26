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
        WebsitesSuite suite = new WebsitesSuite();
        Intent act = RunningActivity.newIntent((AbstractActivity) context, suite);
        if (act != null) {
            context.startActivity(act);
        }
        */


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