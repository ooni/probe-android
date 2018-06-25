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
        initTest();
    }

    public void run(){
        super.run();
    }

    public void initTest(){
        FacebookMessengerTest test = new FacebookMessengerTest();
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
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.facebook_tcp_blocking == null || keys.facebook_dns_blocking == null)
                measurement.state = measurementFailed;
            else if (Boolean.valueOf(keys.facebook_tcp_blocking) || Boolean.valueOf(keys.facebook_dns_blocking))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.facebook_messenger = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}