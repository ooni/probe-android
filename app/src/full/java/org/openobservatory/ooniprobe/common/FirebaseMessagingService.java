package org.openobservatory.ooniprobe.common;

import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;
import org.openobservatory.ooniprobe.R;
import ly.count.android.sdk.messaging.CountlyPush;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	private static final String TAG = "FCM";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Log.d(TAG, "got new message: " + remoteMessage.getData());

		// decode message data and extract meaningful information from it: title, body, badge, etc.
		CountlyPush.Message message = CountlyPush.decodeMessage(remoteMessage.getData());

		if (message != null && message.has("typ")) {
			// custom handling only for messages with specific "typ" keys
			message.recordAction(getApplicationContext());
			return;
		}

		Boolean result = CountlyPush.displayMessage(getApplicationContext(), message, R.drawable.notification_icon, null);
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
