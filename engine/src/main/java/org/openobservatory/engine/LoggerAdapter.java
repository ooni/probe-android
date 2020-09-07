package org.openobservatory.engine;

/** LoggerAdapter adapts engine.Logger to oonimkall.Logger */
class LoggerAdapter implements oonimkall.Logger {
    private Logger logger;

    public LoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }
}
