package org.openobservatory.ooniprobe.model;

public class TestResult {
    public final String input;
    public int anomaly;

    public TestResult(String input, int anomaly){
        this.input = input;
        this.anomaly = anomaly;
    }
}
