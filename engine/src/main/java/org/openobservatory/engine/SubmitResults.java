package org.openobservatory.engine;

/** SubmitResults contains the results of submitting a measurement. */
class SubmitResults {
    protected oonimkall.SubmitResults results;

    protected SubmitResults(oonimkall.SubmitResults results) {
        this.results = results;
    }

    /** getUpdatedMeasurement returns a copy of the original measurement
     * in which the report ID has been updated. */
    public String getUpdatedMeasurement() {
        return results.getUpdatedMeasurement();
    }

    /** getUpdatedReportID returns the updated report ID. */
    public String getUpdatedReportID() {
        return results.getUpdatedReportID();
    }
}
