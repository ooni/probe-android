package org.openobservatory.engine;

final class LoggerNull implements OONILogger {
    @Override
    public void debug(String message) {
    }

    @Override
    public void info(String message) {
    }

    @Override
    public void warn(String message) {
    }
}
