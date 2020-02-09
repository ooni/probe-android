package org.openobservatory.ooniprobe.common;

public class ExceptionManager {
    /*
//TODO
- add sentry
- call this class in the code (Crashlytics.logException)
*/
    public void logException(Exception e){
        Crashlytics.logException(e);
    }
}
