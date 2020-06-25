package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.openobservatory.ooniprobe.R;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            //TODO set to false on production
            if (preferences.getBoolean(context.getString(R.string.automated_testing_enabled), true)){
                AlarmService.setRecurringAlarm(context);
            }
        }
    }

}
