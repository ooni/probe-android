package org.openobservatory.ooniprobe.model;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import junit.framework.Test;

import org.openobservatory.ooniprobe.common.Application;

import java.util.Date;

@Table(database = Application.class)
public class Measurement extends BaseModel {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public double duration;
	@Column public boolean anomaly;
	@Column public String name;
	@Column public String ip;
	@Column public String asn;
	@Column public String asnName;
	@Column public String country;
	@Column public String networkName;
	@Column public String networkType;
	@Column public String reportId;
	@Column public String input;
	@Column public String category;
	@Column public Date startTime;
	@Column public State state;
	@Column public String testKeys;
	private TestKeys testKeysObj;

	@ForeignKey(stubbedRelationship = true)
	public Result result;

	public Measurement() {
	}

	public Measurement(Result result, String name) {
		this.result = result;
		this.name = name;
		startTime = new java.util.Date();
		state = State.FAILED;
	}

	/*
	//TODO-ALE these are method for iOS, maybe in Android can be simplified
	Three scenarios:
    I'm running the test, I start the empty summary, I add stuff and save
    I'm running the test, there is data in the summary, I add stuff and save
    I have to get the summary of an old test and don't modify it
	*/
	public TestKeys getTestKeysObj() {
		if (testKeysObj == null) {
			if (testKeys != null)
				testKeysObj = new Gson().fromJson(testKeys, TestKeys.class);
			else
				testKeysObj = new TestKeys();
		}
		return testKeysObj;
	}

	public void setTestKeysObj(TestKeys testKeysObj){
		this.testKeysObj = testKeysObj;
		testKeys = new Gson().toJson(testKeysObj);
	}

	@Override public boolean delete() {
		//TODO delete logFile and jsonFile
		return super.delete();
	}

	// The possible states of a measurements are:
	//  active, while the measurement is in progress
	//  done, when it's finished, but not necessarily uploaded
	//  uploaded, if it has been uploaded successfully
	//  processed, if the pipeline has processed the measurement
	public enum State {
		ACTIVE,
		FAILED,
		DONE,
		UPLOADED,
		PROCESSED
	}

}
