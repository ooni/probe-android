package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import javax.inject.Inject;

public class ConnectivityChangeUtil extends ConnectivityManager.NetworkCallback {
    @Inject
    NetworkChangeProcessor networkChangeProcessor;

    private static final String TAG = ConnectivityChangeUtil.class.getSimpleName();
    private final NetworkRequest mNetworkRequest;
    private final ConnectivityManager mConnectivityManager;

    // Constructor
    public ConnectivityChangeUtil(Context context) {
        mNetworkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build();

        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Application app = ((Application) context.getApplicationContext());
        app.component.serviceComponent().inject(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            networkChangeProcessor.processNetworkPossibleNetworkChange();
        }
        Log.d(TAG, "onAvailable() called: Connected to network");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.e(TAG, "onLost() called: Lost network connection");
    }

    /**
     * Check current Network state
     */
    public void checkNetworkState() {
        try {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                networkChangeProcessor.processNetworkPossibleNetworkChange();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Registers the Network-Request callback
     * (Note: Register only once to prevent duplicate callbacks)
     */
    public void registerNetworkCallbackEvents() {
        Log.d(TAG, "registerNetworkCallbackEvents() called");
        mConnectivityManager.registerNetworkCallback(mNetworkRequest, this);
    }
}