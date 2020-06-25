package org.openobservatory.ooniprobe.common;

public class ExceptionManager {
    public static void logException(Exception e){
        //TODO-COUNTLY migrate from Crashlytics to Countly
        Crashlytics.logException(e);
    }
}
