package org.openobservatory.engine;

/**
 * OONIMKTaskConfig is the interface that any settings for MK-like tasks
 * must implement. It allows the engine to discover the name of the task
 * that we want to run and to obtain its serialization.
 */
public interface OONIMKTaskConfig {
    /**
     * serialization returns the JSON serialization of the task config, which
     * must be compatible with Measurement Kit v0.9.0 specification.
     */
    String serialization();
}
