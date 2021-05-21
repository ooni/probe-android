package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;

public class ReachabilityManager {
    public static final String WIFI = "wifi";
    public static final String MOBILE = "mobile";
    public static final String NO_INTERNET = "no_internet";

    public static String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return NO_INTERNET;
        else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI)
                return WIFI;
            else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE)
                return MOBILE;
            else
                return NO_INTERNET;
        }
    }

    public static Boolean noInternetAccess(Context context){
        return getNetworkType(context).equals(NO_INTERNET);
    }

    public static Boolean isOnWifi(Context context){
        return getNetworkType(context).equals(WIFI);
    }


    public static Boolean isVPNinUse(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = null;
        activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (caps == null) return false;
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }

    /* From https://developer.android.com/training/monitoring-device-state/battery-monitoring */
    public static Boolean isCharging(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }
}
