package org.openobservatory.ooniprobe.common;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ExceptionManager {
    public static void logException(Exception e){
        FirebaseCrashlytics.getInstance().recordException(e);
    }
}
