package org.openobservatory.ooniprobe.model;

import java.sql.Date;

public class Measurement {

    // The possible states of a measurements are:
    //  active, while the measurement is in progress
    //  done, when it's finished, but not necessarily uploaded
    //  uploaded, if it has been uploaded successfully
    //  processed, if the pipeline has processed the measurement
    public enum MeasurementState
    {
        measurementActive,
        measurementFailed,
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
    MeasurementState state;
    Boolean anomaly;
    Result result;
    String reportId;
    String input;
    String category;

    //defaultValuesForEntity
    /*
    "startTime": date now
    "duration" : 0
    "anomaly" : false
    */


    public void setStartTimeWithUTCstr (float value) {
        //TODO set startTime with UTC string
    }


    public String getFile(String ext){
        //TODO get file with ext json or log
        return "";
    }

    public String getReportFile(String ext){
        return getFile("json");
    }

    public String getLogFile(String ext){
        return getFile("log");
    }


    public void save() {

    }

    public void deleteObject() {
        //TODO delete logFile and jsonFile
    }

}
