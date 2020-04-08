package org.openobservatory.engine;

/** CollectorResults contains the results of speaking with the OONI collector. */
public interface CollectorResults {
    /** isGood returns whether we succeded. */
    public boolean isGood();

    /** getReason returns the reason for failure. */
    public String getReason();

    /** getLogs returns the logs as one-or-more newline-separated
     * lines containing only UTF-8 characters. */
    public String getLogs();

    /** getUpdatedSerializedMeasurement returns the serialized measurement
     * where all the fields that should have been updated, e.g., the
     * report ID, have already been updated with the new values provided
     * by the OONI collector as part of resubmitting. */
    public String getUpdatedSerializedMeasurement();

    /** getUpdatedReportID returns the updated report ID. */
    public String getUpdatedReportID();
}
