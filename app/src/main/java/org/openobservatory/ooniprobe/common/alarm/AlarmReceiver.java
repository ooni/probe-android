package org.openobservatory.ooniprobe.common.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO send notification before the test
        //NotificationService.sendNotification(context, "", null);

        //TODO run more than one test
        WebsitesSuite suite = new WebsitesSuite();
        Intent i = RunningActivity.newIntent(context, suite);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}