package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println(DEBUG_TAG);
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
    }
}