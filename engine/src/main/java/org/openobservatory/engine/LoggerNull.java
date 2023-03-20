package org.openobservatory.engine;

final class LoggerNull implements OONILogger {
    @Override
    public void debug(String message) {
        // nothing
    }

    @Override
    public void info(String message) {
        // nothing
    }

    @Override
    public void warn(String message) {
        // nothing
    }
}
