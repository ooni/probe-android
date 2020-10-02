package org.openobservatory.engine;

import oonimkall.SubmitMeasurementResults;

/**
 * OONISubmitResults contains the results of a single measurement submission
 * to the OONI backends using the OONI collector API.
 */
public final class OONISubmitResults {
    /** updatedMeasurement is a copy of the original measurement
     * in which the report ID has been updated. */
    public String updatedMeasurement;

    /** updatedReportID returns the updated report ID. */
    public String updatedReportID;

    protected OONISubmitResults(SubmitMeasurementResults r) {
        updatedMeasurement = r.getUpdatedMeasurement();
        updatedReportID = r.getUpdatedReportID();
    }
}
