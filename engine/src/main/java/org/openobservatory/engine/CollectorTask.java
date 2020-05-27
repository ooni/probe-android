package org.openobservatory.engine;

/** CollectorTask is a task that interacts with the OONI collector */
public interface CollectorTask {
    /** maybeDiscoverAndSubmit submits a measurement and returns the
     * results. This method will automatically discover a collector, if
     * none is specified. */
    public CollectorResults maybeDiscoverAndSubmit(String measurement, long timeout);
}
