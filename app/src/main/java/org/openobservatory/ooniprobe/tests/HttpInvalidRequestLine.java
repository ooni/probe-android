package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest;

public class HttpInvalidRequestLine extends MKNetworkTest {

    public HttpInvalidRequestLine(Context context){
        //TODO how to call super init?
        super(context);
    }

    public void run(){
        super.run();
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
    public void onEntry(String entry){
        JSONObject jsonObj = super.onEntry(entry);
        if(jsonObj != null) {
            try {
                int anomaly = TestUtility.ANOMALY_GREEN;
                JSONObject jsonObj = new JSONObject(entry);
                JSONObject test_keys = jsonObj.getJSONObject("test_keys");
                if (test_keys.has("tampering")) {
                    Boolean tampering = test_keys.getBoolean("tampering");
                    if (tampering == null)
                        anomaly = TestUtility.ANOMALY_ORANGE;
                    else if (tampering == true)
                        anomaly = TestUtility.ANOMALY_RED;
                }
                if (test.anomaly < anomaly) {
                    test.anomaly = anomaly;
                    //TestStorage.setAnomaly(context, test.test_id, anomaly);
                }
            } catch (JSONException e) {
            }
        }
    }

}
