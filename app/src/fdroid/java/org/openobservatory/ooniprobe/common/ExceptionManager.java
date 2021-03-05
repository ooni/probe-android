package org.openobservatory.ooniprobe.common;

import ly.count.android.sdk.Countly;

import io.sentry.Sentry;

public class ExceptionManager {
    public static void logException(Exception e){
        Countly.sharedInstance().crashes().recordHandledException(e);
        Sentry.captureException(e);
    }
}
