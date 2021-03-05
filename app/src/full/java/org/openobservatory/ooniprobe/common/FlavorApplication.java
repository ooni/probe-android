package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

class FlavorApplication {
	public static void onCreate(Application app) {
		FirebaseApp.initializeApp(app);
		NotificationService.initNotification(app);
		FlavorApplication.reloadCrashConsent(app, app.getPreferenceManager());
	}

	public static void reloadCrashConsent(Context ctx, PreferenceManager preferenceManager) {
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(preferenceManager.isSendCrash());
	}
}
