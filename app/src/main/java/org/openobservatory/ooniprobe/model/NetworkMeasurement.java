package org.openobservatory.ooniprobe.model;

/**
 * Created by lorenzo on 26/04/16.
 */
public class NetworkMeasurement {
    public final String testName;
    public boolean completed = false;
    public final long test_id;

    public final String json_file;
    public final String log_file;
    //public final String status;

    public NetworkMeasurement(String name){
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-"+ test_id +".log";
        this.json_file = "/test-"+ test_id +".json";

    }
}
