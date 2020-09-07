package org.openobservatory.engine;

/** Logger is the common interface for all loggers. */
public interface Logger {
    void debug(String message);
    void info(String message);
    void warn(String message);
}
