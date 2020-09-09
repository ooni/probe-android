package org.openobservatory.engine;

/**
 * OONIMakeSubmitterTask allows you to construct a OONISubmitter. That is, a struct
 * that you will use to submit measurements to the OONI collector.
 */
public interface OONIMakeSubmitterTask extends AutoCloseable, OONICancellable {
    OONISubmitter run() throws OONIException;
}
