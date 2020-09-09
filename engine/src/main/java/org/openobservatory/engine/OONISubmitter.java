package org.openobservatory.engine;

/**
 * OONISubmitter allows you to create a OONISubmitMeasurementTask. That is, a task
 * allowing to submit a single measurements to OONI's collector.
 */
public interface OONISubmitter extends AutoCloseable {
    OONISubmitMeasurementTask newSubmitMeasurementTask(long timeout) throws OONIException;
}
