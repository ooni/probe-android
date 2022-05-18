package org.openobservatory.ooniprobe.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NetworkChangeProcessor;

import javax.inject.Inject;

public class ConnectivityReceiver extends BroadcastReceiver {
    @Inject
    NetworkChangeProcessor networkChangeProcessor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Application app = ((Application) context.getApplicationContext());
        app.component.serviceComponent().inject(this);
        final String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                networkChangeProcessor.processNetworkPossibleNetworkChange();
            }
        }
    }

    public static IntentFilter intentFilter(){
        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();
        // Add network connectivity change action.
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        // Set broadcast receiver priority.
        intentFilter.setPriority(100);
        return intentFilter;
    }
}