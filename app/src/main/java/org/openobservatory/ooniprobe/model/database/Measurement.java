package org.openobservatory.ooniprobe.model.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	@Column public boolean is_anomaly;
	@Column public String test_keys;
	@ForeignKey(saveForeignKeyModel = true) public Url url;
	@ForeignKey(saveForeignKeyModel = true, stubbedRelationship = true) public Result result;
	private transient TestKeys testKeys;
	private transient AbstractTest test;

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

	public static Where<Measurement> selectUploadable() {
		// We check on both the report_id and is_uploaded as we
		// may have some unuploaded measurements which are marked
		// as is_uploaded = true, but we always know that those with
		// report_id set to null are not uploaded
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.is_failed.eq(false))
				.and(Measurement_Table.is_rerun.eq(false))
				.and(Measurement_Table.is_done.eq(true))
				.and(OperatorGroup.clause()
						.or(Measurement_Table.is_uploaded.eq(false))
						.or(Measurement_Table.report_id.isNull())
				);
	}

	public static Where<Measurement> selectUploaded() {
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.is_failed.eq(false))
				.and(Measurement_Table.is_rerun.eq(false))
				.and(Measurement_Table.is_done.eq(true))
				.and(OperatorGroup.clause()
						.or(Measurement_Table.is_uploaded.eq(true))
						.or(Measurement_Table.report_id.isNotNull())
				);
	}

	public static Where<Measurement> selectUploadableWithResultId(int resultId) {
		return Measurement.selectUploadable().and(Measurement_Table.result_id.eq(resultId));
	}

	public static List<Measurement> selectMeasurementsWithJson(Context c) {
		Where<Measurement> msmQuery = Measurement.selectUploaded();
		List<Measurement> measurementsJson = new ArrayList<>();
		List<Measurement> measurements = msmQuery.queryList();
		for (Measurement measurement : measurements){
			if (measurement.hasReportFile(c))
				measurementsJson.add(measurement);
		}
		return measurementsJson;
	}

	public static File getEntryFile(Context c, int measurementId, String test_name) {
		return new File(getMeasurementDir(c), measurementId + "_" + test_name + ".json");
	}

	public void deleteEntryFile(Context c){
		try {
			Measurement.getEntryFile(c, this.id, this.test_name).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean hasReportFile(Context c){
		return Measurement.getEntryFile(c, this.id, this.test_name).exists();
	}

	public static File getLogFile(Context c, int resultId, String test_name) {
		return new File(getMeasurementDir(c), resultId + "_" + test_name + ".log");
	}

	public void deleteLogFile(Context c){
		try {
			Measurement.getLogFile(c, this.result.id, this.test_name).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static File getMeasurementDir(Context c) {
		return new File(c.getFilesDir(), Measurement.class.getSimpleName());
	}

	public AbstractTest getTest() {
		if (test == null)
			switch (test_name) {
				case FacebookMessenger.NAME:
					test = new FacebookMessenger();
					break;
				case Telegram.NAME:
					test = new Telegram();
					break;
				case Whatsapp.NAME:
					test = new Whatsapp();
					break;
				case HttpHeaderFieldManipulation.NAME:
					test = new HttpHeaderFieldManipulation();
					break;
				case HttpInvalidRequestLine.NAME:
					test = new HttpInvalidRequestLine();
					break;
				case WebConnectivity.NAME:
					test = new WebConnectivity();
					break;
				case Ndt.NAME:
					test = new Ndt();
					break;
				case Dash.NAME:
					test = new Dash();
					break;
			}
		return test;
	}

	@NonNull public TestKeys getTestKeys() {
		if (testKeys == null)
			testKeys = test_keys == null ? new TestKeys() : new Gson().fromJson(test_keys, TestKeys.class);
		return testKeys;
	}

	public boolean isUploaded() {
		return is_uploaded && report_id != null;
	}

	public void setTestKeys(TestKeys testKeys) {
		test_keys = new Gson().toJson(testKeys);
	}

	public void deleteUploadedJsons(Application a){
		List<Measurement> measurements = Measurement.selectMeasurementsWithJson(a);
		for (int i = 0; i < measurements.size(); i++) {
			Measurement measurement = measurements.get(i);
			a.getApiClient().getMeasurement(measurement.report_id, null).enqueue(new GetMeasurementsCallback() {
				@Override
				public void onSuccess(ApiMeasurement.Result result) {
					measurement.deleteEntryFile(a);
					measurement.deleteLogFile(a);
				}

				@Override
				public void onError(String msg) {
					/* NOTHING */
				}
			});
		}
	}
}
