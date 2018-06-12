package org.openobservatory.ooniprobe.model;

import android.content.Context;

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
    int id;
    public String name;
    public Date startTime;
    public float duration;
    public String ip;
    public String asn;
    public String asnName;
    public String country;
    public String networkName;
    public String networkType;
    public MeasurementState state;
    public Boolean anomaly;
    public Result result;
    public String reportId;
    public String input;
    public String category;

    //defaultValuesForEntity
    /*
    "startTime": date now
    "duration" : 0
    "anomaly" : false
    */


    public void setStartTimeWithUTCstr (float value) {
        //TODO set startTime with UTC string
    }


    public String getFile(Context ctx, String ext){
        if (this.name == Test.WEB_CONNECTIVITY)
            return ctx.getFilesDir() + this.name + "-" + this.result.id + ext;
        return ctx.getFilesDir() + this.name + "-" + this.id + ext;
    }

    public String getReportFile(Context ctx){
        return getFile(ctx,".json");
    }

    public String getLogFile(Context ctx){
        return getFile(ctx,".log");
    }


    public void save() {

    }

    public void deleteObject() {
        //TODO delete logFile and jsonFile
    }

}
