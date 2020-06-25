package org.openobservatory.ooniprobe.common;

import ly.count.android.sdk.Countly;

public class ExceptionManager {
    public static void logException(Exception e){
        //TODO-COUNTLY which one? https://support.count.ly/hc/en-us/articles/360037754031-Android#logging-handled-exceptions
        Countly.sharedInstance().crashes().recordHandledException(e);
        Countly.sharedInstance().crashes().recordUnhandledException(e);
    }
}
