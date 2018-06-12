package org.openobservatory.ooniprobe.tests;

public class WebConnectivity extends MKNetworkTest {

    public WebConnectivity(){
        //TODO how to call super init?
    }

    public void run(){
        super.run();
    }

    public void runTest(){
    }
/*
    public static void onEntry(String entry){
        if(!test.entry) {
            //TestStorage.setEntry(context, test);
            test.entry = true;
        }
        try {
            int anomaly = TestUtility.ANOMALY_GREEN;
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            Object blocking = test_keys.get("blocking");
            if(blocking instanceof String)
                anomaly = TestUtility.ANOMALY_RED;
            else if(blocking instanceof Boolean)
                anomaly = TestUtility.ANOMALY_GREEN;
            else
                anomaly = TestUtility.ANOMALY_ORANGE;
            if (test.anomaly < anomaly) {
                test.anomaly = anomaly;
                //TestStorage.setAnomaly(context, test.test_id, anomaly);
            }
        } catch (JSONException e) {
        }
    }
*/
}