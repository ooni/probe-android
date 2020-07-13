package org.openobservatory.ooniprobe.common;

import android.content.Context;
import com.google.firebase.FirebaseApp;

class FlavorApplication {
	public static void onCreate(Context context) {
		FirebaseApp.initializeApp(context);
		NotificationService.initNotification(context);
	}
}
