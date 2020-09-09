package org.openobservatory.engine;

/** OONICollectorResults contains the results of speaking with the OONI collector. */
public interface OONICollectorResults {
    /** isGood returns whether we succeeded. */
    boolean isGood();

    /** getReason returns the reason for failure. */
    String getReason();

    /** getLogs returns the logs as one-or-more newline-separated
     * lines containing only UTF-8 characters. */
    String getLogs();

    /** getUpdatedSerializedMeasurement returns the serialized measurement
     * where all the fields that should have been updated, e.g., the
     * report ID, have already been updated with the new values provided
     * by the OONI collector as part of resubmitting. */
    String getUpdatedSerializedMeasurement();

    /** getUpdatedReportID returns the updated report ID. */
    String getUpdatedReportID();
}
