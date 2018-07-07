package org.openobservatory.ooniprobe.model;

import java.util.Date;
import java.util.Random;

public class Measurement {
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
	public Boolean anomaly;
	//TODO this should be an array of results
	public Result result;
	public String reportId;
	public String input;
	public String category;
	int id;
	public Measurement() {
		Random random = new Random();
		this.id = random.nextInt(); // TODO get id from abstract test
		//defaultValuesForEntity
		this.anomaly = false;
		this.duration = 0;
		this.startTime = new java.util.Date();
	}

	public void setStartTimeWithUTCstr(Date value) {
		//TODO set startTime with UTC string
		//input 2018-06-21 13:37:16
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
