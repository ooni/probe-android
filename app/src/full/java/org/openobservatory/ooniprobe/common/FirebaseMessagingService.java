package org.openobservatory.ooniprobe.common;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.OoniRunActivity;

import ly.count.android.sdk.messaging.CountlyPush;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	private static final String TAG = "FCM";
	//TODO-COUNTLY prevent data collection https://firebase.google.com/docs/cloud-messaging/android/client#manifest

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Log.d("DemoFirebaseService", "got new message: " + remoteMessage.getData());

		// decode message data and extract meaningful information from it: title, body, badge, etc.
		CountlyPush.Message message = CountlyPush.decodeMessage(remoteMessage.getData());

		if (message != null && message.has("typ")) {
			// custom handling only for messages with specific "typ" keys
			message.recordAction(getApplicationContext());
			return;
		}

		//Handle ooni run JSON (maybe not needed)
		Intent notificationIntent = null;
		 if (message.has("type") && message.data("type").equals("ooni_run")) {
			 Log.i(TAG, "It's a OONIRun message!");
			 notificationIntent = new Intent(getApplicationContext(), OoniRunActivity.class);
			 notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 notificationIntent.putExtra("mv",  message.data("mv"));
			 notificationIntent.putExtra("ta",  message.data("ta"));
			 notificationIntent.putExtra("tn",  message.data("tn"));
		}

		Boolean result = CountlyPush.displayMessage(getApplicationContext(), message, R.drawable.notification_icon, notificationIntent);
		if (result == null) {
			Log.i(TAG, "Message wasn't sent from Countly server, so it cannot be handled by Countly SDK");
		} else if (result) {
			Log.i(TAG, "Message was handled by Countly SDK");
		} else {
			Log.i(TAG, "Message wasn't handled by Countly SDK because API level is too low for Notification support or because currentActivity is null (not enough lifecycle method calls)");
		}
	}

/*
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "Message from: " + remoteMessage.getFrom());
		//Log.d(TAG, "Message data notification: " + remoteMessage.getNotification().getBody());
		Log.d(TAG, "Message data notification: " + remoteMessage.getNotification());
		Log.d(TAG, "Message data payload: " + remoteMessage.getData());
		Map<String, String> params = remoteMessage.getData();
		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			try {
				//TODO how to handle properly https://github.com/firebase/quickstart-android/blob/e9197f731e13b78dc29d95102af201347634aac2/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.java#L58-L101
				NotificationService.sendNotification(this, remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), 0);

				//TODO-FUTURE we can use click_action instead of type
				JSONObject data = new JSONObject(params.toString());
				//TODO change these parameters
			  if (data.getString("type").equals("ooni_run")) {
					Intent intent = new Intent(getApplicationContext(), OoniRunActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("mv",  params.get("mv"));
					intent.putExtra("tn",  params.get("tn"));
					//intent.putExtra("message", remoteMessage.getNotification().getBody());
					//intent.putExtra("payload", data.getString("payload"));
					getApplicationContext().startActivity(intent);
				}

			} catch (Exception e) {
				System.out.println("JSONException " + e);
			}
		}
	}
*/
	@Override public void onNewToken(String token) {
		((Application) getApplicationContext()).getPreferenceManager().setToken(token);
		CountlyPush.onTokenRefresh(token);
		System.out.println("CountlyPush onNewToken " + token);
	}

	@Override
	public void onDeletedMessages() {
		super.onDeletedMessages();
	}
}
