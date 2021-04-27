package org.openobservatory.ooniprobe.testing;

import kotlin.jvm.functions.Function0;

public class WaitFor {

    public static void waitFor(Check check) {
        Long initialTime = System.currentTimeMillis();
        Throwable lastError;
        do {
            try {
                check.invoke();
                return;
            } catch (Throwable throwable) {
                lastError = throwable;
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (System.currentTimeMillis() - initialTime < TIMEOUT);
        throw new AssertionError("Timeout waiting", lastError);
    }

    public interface Check {
        public void invoke();
    }

    private final static Long TIMEOUT = 10000L;
    private final static Long INTERVAL = TIMEOUT / 20;
}

