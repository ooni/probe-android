package org.openobservatory.ooniprobe.common;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {
	static {
		System.loadLibrary("measurement_kit");
	}

	private PreferenceManager preferenceManager;

	@Override public void onCreate() {
		super.onCreate();
		preferenceManager = new PreferenceManager(this);
		CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(preferenceManager.isSendCrash()).build();
		Fabric.with(this, new Crashlytics.Builder().core(core).build());
		FirebaseApp.initializeApp(this);
	}



}
