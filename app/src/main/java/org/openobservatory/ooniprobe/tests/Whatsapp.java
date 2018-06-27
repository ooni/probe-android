package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.WhatsappTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Whatsapp extends MKNetworkTest {

    public Whatsapp(Context context){
        super(context);
        super.name = Test.WHATSAPP;
        super.measurement.name = super.name;
        initTest();
    }

    public void run(){
        super.run();
    }

    public void initTest(){
        WhatsappTest test = new WhatsappTest();
        this.test = test;
        test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
            @Override
            public void callback(String entry) {
                onEntry(entry);
            }
        });
        super.initCommon();
    }

    /*
         if "whatsapp_endpoints_status", "whatsapp_web_status", "registration_server" are null => failed
         if "whatsapp_endpoints_status" or "whatsapp_web_status" or "registration_server_status" are "blocked" => anomalous
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.whatsapp_endpoints_status == null || keys.whatsapp_web_status == null || keys.registration_server_status == null)
                measurement.state = measurementFailed;
            else if (keys.whatsapp_endpoints_status.equals("blocked") || keys.whatsapp_web_status.equals("blocked") || keys.registration_server_status.equals("blocked"))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.whatsapp = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}
