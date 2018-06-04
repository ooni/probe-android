package org.openobservatory.ooniprobe.model;

import org.json.JSONObject;

public class Summary {
    int totalMeasurements;
    int okMeasurements;
    int failedMeasurements;
    int blockedMeasurements;
    JSONObject json;

    public Summary(){

    }

    public Summary(String summaryJson){

    }

}
