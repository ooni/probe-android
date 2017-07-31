package org.openobservatory.ooniprobe.utils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.BrowserActivity;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.NotificationDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class NotificationsRouter extends FirebaseMessagingService {

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
                JSONObject data = new JSONObject(params.toString());
                if (data.getString("type").equals("open_href")){
                    JSONObject payload = data.getJSONObject("payload");
                    String href = payload.getString("href");
                    JSONArray alt_hrefs = payload.getJSONArray("alt_hrefs");
                    final ArrayList<String>urls = new ArrayList<>();
                    urls.add(href);
                    for(int i=0; i< alt_hrefs.length(); i++)
                    {
                        urls.add(alt_hrefs.getString(i));
                    }
                    Log.d(TAG, "Message data urls: " + urls);
                    Intent intent =  new Intent(getApplicationContext(), NotificationDialog.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("message", remoteMessage.getNotification().getBody());
                    intent.putStringArrayListExtra("urls", urls);
                    getApplicationContext().startActivity(intent);
                }
            }
            catch (Exception e) {
                System.out.println("JSONException "+ e);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        /*
        IntentRouter.getInstance(getApplicationContext())
            .emit_string("orchestrate/notification",
                remoteMessage.getNotification().getBody());
         */
    }

    @Override
    public void onDeletedMessages() {
        // TODO
    }

    private final String TAG = "NotificationsRouter";
}
