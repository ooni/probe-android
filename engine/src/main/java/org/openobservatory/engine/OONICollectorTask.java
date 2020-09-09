package org.openobservatory.engine;

/** OONICollectorTask is a task that interacts with the OONI collector. You MUST NOT use
 * this task in a multi-thread context. Please, make sure you use a single thread.  */
public interface OONICollectorTask {
    /** maybeDiscoverAndSubmit submits a measurement and returns the
     * results. This method will automatically discover a collector, if
     * none is specified. */
    OONICollectorResults maybeDiscoverAndSubmit(String measurement, long timeout);
}
