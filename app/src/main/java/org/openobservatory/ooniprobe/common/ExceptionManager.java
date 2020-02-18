package org.openobservatory.ooniprobe.common;

public class ExceptionManager {
    public static void logException(Exception e){
        Crashlytics.logException(e);
    }
}
