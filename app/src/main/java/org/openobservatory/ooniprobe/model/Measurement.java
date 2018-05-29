package org.openobservatory.ooniprobe.model;

import java.sql.Date;

public class Measurement {

    // The possible states of a measurements are:
    //  active, while the measurement is in progress
    //  done, when it's finished, but not necessarily uploaded
    //  uploaded, if it has been uploaded successfully
    //  processed, if the pipeline has processed the measurement
    public enum MeasurementStatus
    {
        measurementActive,
        measurementDone,
        measurementUploaded,
        measurementProcessed
    }

    String name;
    Date startTime;
    float duration;
    String ip;
    String asn;
    String asnName;
    String country;
    String networkName;
    String networkType;
    MeasurementStatus state;
    int blocking;
    Result result;
    String reportId;
    String input;
    String category;

}
