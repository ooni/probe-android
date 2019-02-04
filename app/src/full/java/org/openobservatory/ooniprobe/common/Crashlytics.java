package org.openobservatory.ooniprobe.common;

public class Crashlytics {
	public static void logException(Throwable throwable) {
		com.crashlytics.android.Crashlytics.logException(throwable);
	}
}
