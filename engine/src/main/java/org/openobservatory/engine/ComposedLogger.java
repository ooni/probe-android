package org.openobservatory.engine;

/** ComposedLogger allows to compose two loggers */
final class ComposedLogger implements OONILogger {
    private OONILogger left, right;

    ComposedLogger(OONILogger left, OONILogger right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void debug(String message) {
        left.debug(message);
        right.debug(message);
    }

    @Override
    public void info(String message) {
        left.info(message);
        right.info(message);
    }

    @Override
    public void warn(String message) {
        left.warn(message);
        right.warn(message);
    }
}
