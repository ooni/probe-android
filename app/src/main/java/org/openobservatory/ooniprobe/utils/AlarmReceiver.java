package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println(DEBUG_TAG);
        NotificationHandler.sendNotification(context, context.getString(R.string.local_notifications_text));
        TestData.getInstance(context, null);
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, TestUtility.WEB_CONNECTIVITY));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, TestUtility.HTTP_HEADER_FIELD_MANIPULATION));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, TestUtility.HTTP_INVALID_REQUEST_LINE));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, TestUtility.NDT));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, TestUtility.DASH));
    }

}