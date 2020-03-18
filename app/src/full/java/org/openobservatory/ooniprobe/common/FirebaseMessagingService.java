package org.openobservatory.ooniprobe.common;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.openobservatory.ooniprobe.activity.NotificationDialogActivity;

import java.util.Map;

import ly.count.android.sdk.messaging.CountlyPush;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	private static final String TAG = "FCM";
	//TODO prevent data collection https://firebase.google.com/docs/cloud-messaging/android/client#manifest

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "Message from: " + remoteMessage.getFrom());
		Log.d(TAG, "Message data payload: " + remoteMessage.getData());
		Map<String, String> params = remoteMessage.getData();
		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			try {

				//TODO how to handle properly https://github.com/firebase/quickstart-android/blob/e9197f731e13b78dc29d95102af201347634aac2/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.java#L58-L101
				//NotificationService.sendNotification(this, remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), null);

				//TODO-FUTURE we can use click_action instead of type
				JSONObject data = new JSONObject(params.toString());
				if (data.getString("type").equals("open_href")) {
					Intent intent = new Intent(getApplicationContext(), NotificationDialogActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("message", remoteMessage.getNotification().getBody());
					intent.putExtra("payload", data.getString("payload"));
					getApplicationContext().startActivity(intent);
				}
			} catch (Exception e) {
				System.out.println("JSONException " + e);
			}
		}
	}

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
