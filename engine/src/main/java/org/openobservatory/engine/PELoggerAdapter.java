package org.openobservatory.engine;

import oonimkall.Logger;

final class PELoggerAdapter implements Logger {
    private OONILogger logger;

    public PELoggerAdapter(OONILogger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }
}
