package org.openobservatory.ooniprobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import org.openobservatory.ooniprobe.common.Application;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
			if (!((Application) context.getApplicationContext()).getPreferenceManager().getNetworkType().equals(NotificationService.NO_INTERNET))
				NotificationService.sendRegistrationToServer((Application) context.getApplicationContext());
	}
}
