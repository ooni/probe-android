package org.openobservatory.ooniprobe.common;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;

public class FlavorApplication {
	public static void onCreate(Context context, boolean sendCrash) {
		CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(sendCrash).build();
		Fabric.with(context, new Crashlytics.Builder().core(core).build());
		FirebaseApp.initializeApp(context);
	}
}
