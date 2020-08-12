package org.openobservatory.ooniprobe.common;

import ly.count.android.sdk.Countly;

public class ExceptionManager {
    public static void logException(Exception e){
        Countly.sharedInstance().crashes().recordHandledException(e);
    }
}
