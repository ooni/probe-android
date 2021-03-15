package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.raizlabs.android.dbflow.config.FlowLog;

import org.openobservatory.ooniprobe.BuildConfig;

import io.sentry.Sentry;
import io.sentry.android.core.SentryAndroid;

class FlavorApplication {
	public static void onCreate(Application app) {
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		FirebaseApp.initializeApp(app);
		NotificationService.initNotification(app);
		CountlyManager.reloadCrashConsent(app);
	}
}
