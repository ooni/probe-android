package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import org.openobservatory.ooniprobe.R;

import io.fabric.sdk.android.Fabric;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.messaging.CountlyPush;

class FlavorApplication {
	public static void onCreate(Context context, boolean sendCrash) {
		//TODO-COUNTLY remove CrashlyticsCore and use sendCrash also in fdroid
		CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(sendCrash).build();
		Fabric.with(context, new Crashlytics.Builder().core(core).build());
		FirebaseApp.initializeApp(context);
		NotificationService.setChannel(context, CountlyPush.CHANNEL_ID, context.getString(R.string.General_AppName));
		//TODO-COUNTLY switch to production
		CountlyPush.init((Application) context, Countly.CountlyMessagingMode.TEST);
		NotificationService.setToken((Application) context);
	}
}
