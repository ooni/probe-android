package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.DeviceId;
import ly.count.android.sdk.messaging.CountlyPush;

class FlavorApplication {
	public static void onCreate(Context context, boolean sendCrash) {
		CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(sendCrash).build();
		Fabric.with(context, new Crashlytics.Builder().core(core).build());
		FirebaseApp.initializeApp(context);


		//NotificationService.setChannel(this);
		// prepare features that should be added to the group
		String[] groupFeatures = new String[]{ Countly.CountlyFeatureNames.sessions, Countly.CountlyFeatureNames.views, Countly.CountlyFeatureNames.crashes, Countly.CountlyFeatureNames.push };

		// create the feature group
		// Countly.sharedInstance().createFeatureGroup("groupName", groupFeatures);
		//TODO disable analytics in debug mode  or use other server
		//Countly.sharedInstance().setRequiresConsent(true);
		CountlyConfig config = new CountlyConfig()
				.setAppKey("fd78482a10e95fd471925399adbcb8ae1a45661f")
				.setContext(context)
				//.setDeviceId("lorenzo")
				//.setDeviceId(null)
				.setIdMode(DeviceId.Type.ADVERTISING_ID)
				//.setIdMode(DeviceId.Type.OPEN_UDID)
				//.setRequiresConsent(true)
				.setConsentEnabled(groupFeatures)
				.setServerURL("https://mia-countly-test.ooni.nu")
				//.setLoggingEnabled(!BuildConfig.DEBUG)
				.setLoggingEnabled(true)
				.setViewTracking(true)
				.setHttpPostForced(true)
				.enableCrashReporting();
		Countly.sharedInstance().init(config);
		CountlyPush.init((Application) context, Countly.CountlyMessagingMode.TEST);
		NotificationService.setToken((Application) context);
        /*
        Deprecated code
        Countly.sharedInstance().init(this, "https://mia-countly-test.ooni.nu", "fd78482a10e95fd471925399adbcb8ae1a45661f", null, DeviceId.Type.ADVERTISING_ID);
        Countly.sharedInstance().initMessaging(this, MainActivity.class, "951667061699", Countly.CountlyMessagingMode.PRODUCTION);
        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();
        */

	}
}
