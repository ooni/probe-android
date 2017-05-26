package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println(DEBUG_TAG);
        NotificationHandler.sendNotification(context, context.getString(R.string.local_notifications_text));
        TestData.getInstance(context, null);
        TestData.doNetworkMeasurements(context, "web_connectivity");
        TestData.doNetworkMeasurements(context, "http_invalid_request_line");
        TestData.doNetworkMeasurements(context, "ndt_test");
    }

}