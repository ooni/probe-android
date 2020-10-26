package org.openobservatory.ooniprobe.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceUtil.scheduleJob(context);
    }
}

