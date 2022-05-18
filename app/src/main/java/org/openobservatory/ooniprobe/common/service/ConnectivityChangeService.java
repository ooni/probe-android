package org.openobservatory.ooniprobe.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.common.ConnectivityChangeUtil;
import org.openobservatory.ooniprobe.receiver.ConnectivityReceiver;

public class ConnectivityChangeService extends Service {

    private static final String TAG = ConnectivityChangeService.class.getSimpleName();
    private ConnectivityReceiver receiver = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            ConnectivityChangeUtil mNetworkMonitoringUtil = new ConnectivityChangeUtil(getApplicationContext());
            // Check the network state before registering for the 'networkCallbackEvents'
//            mNetworkMonitoringUtil.checkNetworkState();
            mNetworkMonitoringUtil.registerNetworkCallbackEvents();
        } else {
            // Create a network change broadcast receiver.
            receiver = new ConnectivityReceiver();
            // Register the broadcast receiver with the intent filter object.
            registerReceiver(receiver, ConnectivityReceiver.intentFilter());
        }

        Log.d(TAG, "Service onCreate: ConnectivityReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister screenOnOffReceiver when destroy.
        if (receiver != null) {
            unregisterReceiver(receiver);
            Log.d(TAG, "Service onDestroy: ConnectivityReceiver is unregistered.");
        }
    }
}