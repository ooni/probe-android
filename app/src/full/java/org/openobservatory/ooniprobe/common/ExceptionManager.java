package org.openobservatory.ooniprobe.common;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.sentry.Sentry;

public class ExceptionManager {
    //TODO unify with Countlymanager
    public static void logException(Exception e){
        FirebaseCrashlytics.getInstance().recordException(e);
        if (Sentry. isEnabled())
            Sentry.captureException(e);
    }
}
