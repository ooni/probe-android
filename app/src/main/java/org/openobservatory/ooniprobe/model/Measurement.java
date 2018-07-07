package org.openobservatory.ooniprobe.model;

import java.util.Date;

public class Measurement {
	public int id;
	public String name;
	public Date startTime;
	public double duration;
	public String ip;
	public String asn;
	public String asnName;
	public String country;
	public String networkName;
	public String networkType;
	public MeasurementState state;
	public boolean anomaly;
	//TODO this should be an array of results
	public Result result;
	public String reportId;
	public String input;
	public String category;

	public Measurement() {
		startTime = new java.util.Date();
	}

	public void save() {
	}

	public void deleteObject() {
		//TODO delete logFile and jsonFile
	}

	// The possible states of a measurements are:
	//  active, while the measurement is in progress
	//  done, when it's finished, but not necessarily uploaded
	//  uploaded, if it has been uploaded successfully
	//  processed, if the pipeline has processed the measurement
	public enum MeasurementState {
		measurementActive,
		measurementFailed,
		measurementDone,
		measurementUploaded,
		measurementProcessed
	}
}
