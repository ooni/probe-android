package org.openobservatory.ooniprobe.common;

import com.google.firebase.FirebaseApp;

class FlavorApplication {
	public static void onCreate(Application app) {
		FirebaseApp.initializeApp(app);
		NotificationService.initNotification(app);
	}
}
