package org.openobservatory.ooniprobe.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Summary {
    @SerializedName("totalMeasurements")
    public int totalMeasurements;

    @SerializedName("okMeasurements")
    public int okMeasurements;

    @SerializedName("failedMeasurements")
    public int failedMeasurements;

    @SerializedName("anomalousMeasurements")
    public int anomalousMeasurements;

    @SerializedName("web_connectivity")
    public JsonResult.TestKeys web_connectivity;

    @SerializedName("whatsapp")
    public JsonResult.TestKeys whatsapp;
    @SerializedName("telegram")
    public JsonResult.TestKeys telegram;
    @SerializedName("facebook_messenger")
    public JsonResult.TestKeys facebook_messenger;

    @SerializedName("http_invalid_request_line")
    public JsonResult.TestKeys http_invalid_request_line;
    @SerializedName("http_header_field_manipulation")
    public JsonResultHttpHeader.TestKeys http_header_field_manipulation;

    @SerializedName("ndt")
    public JsonResult.TestKeys ndt;
    @SerializedName("dash")
    public JsonResult.TestKeys dash;

    public Summary(){

    }

    public static Summary fromJson(String json) {
        return new Gson().fromJson(json, Summary.class);
    }
    
}
