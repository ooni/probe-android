package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;
import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class HttpHeaderFieldManipulation extends MKNetworkTest {

    public HttpHeaderFieldManipulation(Context context){
        super(context);
        super.name = Test.HTTP_HEADER_FIELD_MANIPULATION;
        super.measurement.name = super.name;
        initTest();
    }

    public void run(){
        super.run();
    }

    public void initTest(){
        HttpHeaderFieldManipulationTest test = new HttpHeaderFieldManipulationTest();
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
         onEntry method for http header field manipulation test, check "failure" key
         null => failed
         true => anomalous
         then the keys in the "tampering" object will be checked, if any of them is not null and TRUE, then test is anomalous
          	tampering {
		        header_field_name
		        header_field_number
		        header_field_value
		        header_name_capitalization
		        request_line_capitalization
		        total
	        }
     */
    public void onEntry(String entry) {
        /*
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            JsonResult.TestKeys.tampering tampering = keys.tampering;
            if (tampering == null)
                measurement.state = measurementFailed;
            else if (Boolean.valueOf(tampering.header_field_name) ||
                        Boolean.valueOf(tampering.header_field_number) ||
                        Boolean.valueOf(tampering.header_field_value) ||
                        Boolean.valueOf(tampering.header_name_capitalization) ||
                        Boolean.valueOf(tampering.request_line_capitalization) ||
                        Boolean.valueOf(tampering.total))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.http_header_field_manipulation = keys;
            super.updateSummary();
            measurement.save();
        }*/
    }
}
