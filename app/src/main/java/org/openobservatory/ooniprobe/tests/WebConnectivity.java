package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class WebConnectivity extends MKNetworkTest {

    public WebConnectivity(Context context){
        super(context);
        super.name = Test.WEB_CONNECTIVITY;
        super.measurement.name = super.name;
    }


    public void run(){
        super.run();
        runTest();
    }

    public void runTest(){
        HttpHeaderFieldManipulationTest test = new HttpHeaderFieldManipulationTest();
        test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
            @Override
            public void callback(String entry) {
                onEntry(entry);
            }
        });
        super.initCommon(test);
        //test.run();
    }

    /*
     null => failed
     false => not blocked
     string (dns, tcp-ip, http-failure, http-diff) => anomalous
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.blocking == null)
                measurement.state = measurementFailed;
            //TODO
            // else if (Boolean.valueOf(keys.tampering))
            //    measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.http_invalid_request_line = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}