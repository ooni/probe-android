package org.openobservatory.ooniprobe.common;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import org.openobservatory.ooniprobe.R;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.messaging.CountlyPush;

class FlavorApplication {
	public static void onCreate(Context context) {
		FirebaseApp.initializeApp(context);
		NotificationService.setChannel(context, CountlyPush.CHANNEL_ID, context.getString(R.string.General_AppName));
		//TODO-COUNTLY switch to production
		CountlyPush.init((Application) context, Countly.CountlyMessagingMode.TEST);
		NotificationService.setToken((Application) context);
	}
}
