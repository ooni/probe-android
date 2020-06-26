package org.openobservatory.ooniprobe.common;

import android.content.Context;
import com.google.firebase.FirebaseApp;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.messaging.CountlyPush;

class FlavorApplication {
	public static void onCreate(Context context) {
		FirebaseApp.initializeApp(context);
		NotificationService.setChannel(context, CountlyPush.CHANNEL_ID, context.getString(R.string.General_AppName));
		//TODO-COUNTLY what to do on notification disable?
		CountlyPush.init((Application) context, BuildConfig.DEBUG? Countly.CountlyMessagingMode.TEST:Countly.CountlyMessagingMode.PRODUCTION);
		NotificationService.setToken((Application) context);
	}
}
