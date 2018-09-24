package org.openobservatory.ooniprobe.model.database;

import android.content.Context;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

@Table(database = Application.class)
public class Measurement extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String test_name;
	@Column public Date start_time;
	@Column public double runtime;
	@Column public boolean is_done;
	@Column public boolean is_uploaded;
	@Column public boolean is_failed;
	@Column public String failure_msg;
	@Column public boolean is_upload_failed;
	@Column public String upload_failure_msg;
	@Column public boolean is_rerun;
	@Column public String report_id;
	@Column public Integer measurement_id;
	@Column public boolean is_anomaly;
	@Column public String test_keys;
	@ForeignKey(saveForeignKeyModel = true, deleteForeignKeyModel = true) public Url url;
	@ForeignKey(saveForeignKeyModel = true, deleteForeignKeyModel = true, stubbedRelationship = true) public Result result;

	public Measurement() {
	}

	public Measurement(Result result, String test_name) {
		this.result = result;
		this.test_name = test_name;
		start_time = new java.util.Date();
	}

	public Measurement(Result result, String test_name, String report_id) {
		this.result = result;
		this.test_name = test_name;
		this.report_id = report_id;
		start_time = new java.util.Date();
	}

	public static String getEntryFileName(int measurementId, String test_name) {
		return measurementId + "_" + test_name + ".json";
	}

	public static String getLogFileName(int resultId, String test_name) {
		return resultId + "_" + test_name + ".log";
	}

	public AbstractTest getTest() {
		switch (test_name) {
			case FacebookMessenger.NAME:
				return new FacebookMessenger();
			case Telegram.NAME:
				return new Telegram();
			case Whatsapp.NAME:
				return new Whatsapp();
			case HttpHeaderFieldManipulation.NAME:
				return new HttpHeaderFieldManipulation();
			case HttpInvalidRequestLine.NAME:
				return new HttpInvalidRequestLine();
			case WebConnectivity.NAME:
				return new WebConnectivity();
			case Ndt.NAME:
				return new Ndt();
			case Dash.NAME:
				return new Dash();
			default:
				return null;
		}
	}

	public TestKeys getTestKeys() {
		return new Gson().fromJson(test_keys, TestKeys.class);
	}

	public void setTestKeys(TestKeys testKeys) {
		test_keys = new Gson().toJson(testKeys);
	}

	public void deleteObj(Context c) {
		try {
			new File(c.getFilesDir(), Measurement.getEntryFileName(this.id, this.test_name)).delete();
			new File(c.getFilesDir(), Measurement.getLogFileName(this.result.id, this.test_name)).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.delete();
	}
}
