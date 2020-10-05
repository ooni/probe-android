package org.openobservatory.engine;

import java.util.ArrayList;

/** LoggerArray is a logger that writes logs into an array. */
public final class LoggerArray implements OONILogger {
    public ArrayList<String> logs = new ArrayList<>();

    @Override
    public void debug(String message) {
        logs.add(message);
    }

    @Override
    public void info(String message) {
        logs.add(message);
    }

    @Override
    public void warn(String message) {
        logs.add(message);
    }
}
