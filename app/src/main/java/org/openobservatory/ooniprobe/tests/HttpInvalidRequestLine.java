package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;
import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class HttpInvalidRequestLine extends MKNetworkTest {

    public HttpInvalidRequestLine(Context context){
        super(context);
        super.name = Test.HTTP_INVALID_REQUEST_LINE;
        super.measurement.name = super.name;
    }

    public void run(){
        super.run();
        runTest();
    }

    public void runTest(){
        HttpInvalidRequestLineTest test = new HttpInvalidRequestLineTest();
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
         onEntry method for http invalid request line test, check "tampering" key
         null => failed
         true => anomalous
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.failure != null)
                measurement.state = measurementFailed;
            else if (Boolean.valueOf(keys.tampering))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.http_invalid_request_line = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}