package org.openobservatory.engine;

/** OONISubmitMeasurementTask submits measurements to the OONI collector API. */
public interface OONISubmitMeasurementTask extends AutoCloseable, OONICancellable {
    OONISubmitResults run(String measurement) throws OONIException;
}
