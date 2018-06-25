package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest;
import org.openobservatory.measurement_kit.nettests.WebConnectivityTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class WebConnectivity extends MKNetworkTest {

    public WebConnectivity(Context context){
        super(context);
        super.name = Test.WEB_CONNECTIVITY;
        super.measurement.name = super.name;
        initTest();
    }

    public void run(){
        super.run();
    }

    public void initTest(){
        WebConnectivityTest test = new WebConnectivityTest();
        this.test = test;
        //TODO remove before release
        String inputUrlsPath = context.getFilesDir() + "/global.txt";
        test.set_input_filepath(inputUrlsPath);
        test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
            @Override
            public void callback(String entry) {
                onEntry(entry);
            }
        });
        super.initCommon();
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
            else if (!keys.blocking.equals("false"))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.web_connectivity = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}