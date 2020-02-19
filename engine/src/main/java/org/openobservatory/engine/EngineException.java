package org.openobservatory.engine;

/** EngineException is the exception thrown by the Engine */
public class EngineException extends Exception {
    public EngineException(String message, Throwable exc) {
        super(message, exc);
    }
}
