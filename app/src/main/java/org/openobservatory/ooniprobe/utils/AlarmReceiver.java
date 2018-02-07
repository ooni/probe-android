package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println(DEBUG_TAG);
        NotificationHandler.sendNotification(context, context.getString(R.string.local_notifications_text));
        TestData.getInstance(context, null);
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, OONITests.WEB_CONNECTIVITY));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, OONITests.HTTP_HEADER_FIELD_MANIPULATION));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, OONITests.HTTP_INVALID_REQUEST_LINE));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, OONITests.NDT));
        TestData.doNetworkMeasurements(context, new NetworkMeasurement(context, OONITests.DASH));
    }

}