package org.openobservatory.ooniprobe.test;

import androidx.annotation.VisibleForTesting;

public class EngineProvider {

    @VisibleForTesting
    public static EngineInterface engineInterface = new EngineInterface();

    public static EngineInterface get() {
        return engineInterface;
    }
}
