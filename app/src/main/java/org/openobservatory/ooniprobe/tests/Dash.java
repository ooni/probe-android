package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.DashTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Dash extends MKNetworkTest {

    public Dash(Context context){
        super(context);
        super.name = Test.DASH;
        super.measurement.name = super.name;
    }

    public void run(){
        super.run();
        runTest();
    }

    public void runTest(){
        DashTest test = new DashTest();
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
     onEntry method for dash test, check "failure" key
     !=null => failed
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.failure != null)
                measurement.state = measurementFailed;

            Summary summary = result.getSummary();
            summary.dash = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}
