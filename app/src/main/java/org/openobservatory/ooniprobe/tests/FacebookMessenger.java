package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.FacebookMessengerTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class FacebookMessenger extends MKNetworkTest {

    public FacebookMessenger(Context context){
        super(context);
        super.name = Test.FACEBOOK_MESSENGER;
        super.measurement.name = super.name;
    }

    public void run(){
        super.run();
        runTest();
    }

    public void runTest(){
        FacebookMessengerTest test = new FacebookMessengerTest();
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
     on_entry method for http invalid request line test
     if the "tampering" key exists and is null then anomaly will be set to 1 (orange)
     otherwise "tampering" object exists and is TRUE, then anomaly will be set to 2 (red)
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.tampering == null)
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
