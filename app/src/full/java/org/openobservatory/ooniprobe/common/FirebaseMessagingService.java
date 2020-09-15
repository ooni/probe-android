package org.openobservatory.ooniprobe.common;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.messaging.RemoteMessage;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;

import ly.count.android.sdk.messaging.CountlyPush;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	private static final String TAG = "FCM";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Log.d(TAG, "got new message: " + remoteMessage.getData());

		// decode message data and extract meaningful information from it: title, body, badge, etc.
		CountlyPush.Message message = CountlyPush.decodeMessage(remoteMessage.getData());

		Intent notificationIntent = null;
		if (!message.has("c.l")) {
			notificationIntent = new Intent(this, MainActivity.class);
			notificationIntent.putExtra(MainActivity.NOTIFICATION_DIALOG, "yes");
			notificationIntent.putExtra("title", "title");
			notificationIntent.putExtra("message", "message");
		}
		
		Boolean result = CountlyPush.displayMessage(getApplicationContext(), message, R.drawable.notification_icon, notificationIntent);
		if (result == null) {
			Log.d(TAG, "Message wasn't sent from Countly server, so it cannot be handled by Countly SDK");
		} else if (result) {
			Log.d(TAG, "Message was handled by Countly SDK");
		} else {
			Log.d(TAG, "Message wasn't handled by Countly SDK because API level is too low for Notification support or because currentActivity is null (not enough lifecycle method calls)");
		}
	}

	@Override
	public void onNewToken(String token) {
		super.onNewToken(token);
		((Application) getApplicationContext()).getPreferenceManager().setToken(token);
		CountlyPush.onTokenRefresh(token);
	}
}
