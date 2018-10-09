package org.openobservatory.ooniprobe.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionState {
    private static ConnectionState instance;
    private String network_type;
    public Context context;

    public static ConnectionState getInstance(final Context c) {
        if (instance == null) {
            instance = new ConnectionState();
            instance.context = c;
            instance. network_type = instance.getNetworkTypeFromService();
        }
        return instance;
    }

    //https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    private String getNetworkTypeFromService(){
        String networkType = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "wifi";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = "mobile";
            }
        } else {
            // not connected to the internet
            networkType = "no_internet";
        }
        //Log.d(TAG, networkType);
        return networkType;
    }

    public void updateNetworkType() {
        network_type = getNetworkTypeFromService();
        //TODO-ALE part of refactor class
        //if (!network_type.equals("no_internet"))
        //    NotificationService.getInstance(c).sendRegistrationToServer();
    }

    public String getNetworkType() {
        return network_type;
    }
}
