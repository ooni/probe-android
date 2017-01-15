package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.openobservatory.ooniprobe.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Notifications.sendNotification(context, context.getString(R.string.local_notifications_text));
    }

}