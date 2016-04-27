package io.github.measurement_kit.model;

/**
 * Created by lorenzo on 26/04/16.
 */
public class NetworkMeasurement {
    public final String testName;
    public boolean finished = false;
    public final String fileName;

    public NetworkMeasurement(String name, String file){
        this.testName = name;
        this.fileName = file;
    }
}
