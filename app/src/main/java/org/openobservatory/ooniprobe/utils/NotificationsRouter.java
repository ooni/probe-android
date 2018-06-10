package org.openobservatory.ooniprobe.utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.openobservatory.ooniprobe.activity.NotificationDialog;

import java.util.Map;

public class NotificationsRouter extends FirebaseMessagingService {
	private final String TAG = "NotificationsRouter";

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
				//TODO we can use click_action instead of type
				JSONObject data = new JSONObject(params.toString());
				if (data.getString("type").equals("open_href")) {
					Intent intent = new Intent(getApplicationContext(), NotificationDialog.class);
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

	@Override
	public void onDeletedMessages() {
	}
}
