package org.openobservatory.engine;

/**
 * OONISession contains shared state for running experiments and/or other
 * related task (e.g. geolocation). Note that the OONISession isn't
 * mean to be shared across thread. It is also not meant to be a long
 * living object. The workflow is to create a OONISession, do the operations
 * you need to do with it now, then call Session.close. All of this is
 * supposed to happen within the same Java thread. If you need to cancel
 * any operation from other threads, all tasks have a cancel method.
 */
public interface OONISession extends AutoCloseable {
    /**
     * newGeolocateTask creates a new OONIGeolocateTask. This task will allow you
     * to geolocate the probe. The timeout for the task is in seconds. When
     * the timeout value is zero or negative, there won't be any timeout.
     */
    OONIGeolocateTask newGeolocateTask(long timeout) throws OONIException;

    /**
     * newMakeSubmitterTask creates a new OONIMakeSubmitterTask. This task will
     * allow you to create a OONISubmitter. That is an object that allows
     * you to submit measurements to the OONI collector. The timeout for the
     * task has exactly the same semantics of newGeolocateTask.
     */
    OONIProbeServicesClient newProbeServicesClient(long timeout) throws OONIException;
}
