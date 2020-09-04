package org.openobservatory.engine;

/** Logger is the common interface for all loggers. */
public interface Logger extends oonimkall.Logger {
    // Methods defined in oonimkall.Logger are repeated for convenience
    void debug(String message);
    void info(String message);
    void warn(String message);
}
