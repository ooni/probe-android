package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

}
