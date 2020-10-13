package org.openobservatory.engine;

/** OONILogger is the logger used by a OONISession. */
public interface OONILogger {
    /** debug emits a debug message */
    void debug(String message);

    /** info emits an informational message */
    void info(String message);

    /** warn emits a warning message */
    void warn(String message);
}
