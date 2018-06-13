package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Test;
import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.*;

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
            super.updateSummary();
            setTestSummary(keys);
            measurement.save();
        }
    }

    public void setTestSummary(JsonResult.TestKeys keys) {
        /*
            Summary *summary = [self.result getSummary];
    NSMutableDictionary *values = [[NSMutableDictionary alloc] init];
    if ([keys safeObjectForKey:@"sent"]){
        [values setObject:[keys safeObjectForKey:@"sent"] forKey:@"sent"];
    }
    if ([keys safeObjectForKey:@"received"]){
        [values setObject:[keys safeObjectForKey:@"received"] forKey:@"received"];
    }
    [summary.json setValue:values forKey:self.name];
    [self.result save];

         */
    }
}
