package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.raizlabs.android.dbflow.config.FlowLog;

import org.openobservatory.ooniprobe.BuildConfig;

import io.sentry.android.core.SentryAndroid;

class FlavorApplication {
	public static void onCreate(Application app) {
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		else {
			SentryAndroid.init(app,
					options -> {
						options.setDsn("https://9dcd83d9519844188803aa817cdcd416@o155150.ingest.sentry.io/5619989");
						options.setBeforeSend(
								(event, hint) -> {
									// Drop an event altogether:
									if (!app.getPreferenceManager().isSendCrash()) {
										return null;
									}
									return event;
								});
					});
		}
		FirebaseApp.initializeApp(app);
		NotificationService.initNotification(app);
		FlavorApplication.reloadCrashConsent(app, app.getPreferenceManager());
	}

	public static void reloadCrashConsent(Context ctx, PreferenceManager preferenceManager) {
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(preferenceManager.isSendCrash());
	}
}
