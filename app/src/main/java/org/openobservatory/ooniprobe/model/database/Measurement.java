package org.openobservatory.ooniprobe.model.database;

import static com.google.common.collect.Lists.newArrayList;

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

import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.common.AppDatabase;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Experimental;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Tor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(database = AppDatabase.class)
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
	@Column public String rerun_network;
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

	private static Where<Measurement> selectDone() {
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.is_rerun.eq(false))
				.and(Measurement_Table.is_done.eq(true));
	}

	public static Where<Measurement> selectUploadable() {
		// We check on both the report_id and is_uploaded as we
		// may have some non-uploaded measurements which are marked
		// as is_uploaded = true, but we always know that those with
		// report_id set to null are not uploaded
		return selectDone()
				.and(OperatorGroup.clause()
						.or(Measurement_Table.is_uploaded.eq(false))
						.or(Measurement_Table.report_id.isNull())
				);
	}

	public static Where<Measurement> selectUploaded() {
		return SQLite.select().from(Measurement.class)
				.where(OperatorGroup.clause()
						.or(Measurement_Table.is_uploaded.eq(true))
						.or(Measurement_Table.report_id.isNotNull())
				);
	}

	/*
	 * Given a sql query, check whenever at least a non upload measurement set has the report file on disk
	 * This is needed to avoid showing the snackbar when there are measurements marked as non uploaded but with no file on disk
	 */
	public static boolean hasReport(Context c, Where<Measurement> msmQuery) {
		List<Measurement> measurements = msmQuery.queryList();
		for (Measurement measurement : measurements){
			if (measurement.hasReportFile(c))
				return true;
		}
		return false;
	}

	public static List<Measurement> withReport(Context c, Where<Measurement> msmQuery) {
		List<Measurement> measurements = msmQuery.queryList();
		List<Measurement> toRemove = newArrayList();
		for (Measurement measurement : measurements){
			if (!measurement.hasReportFile(c))
				toRemove.add(measurement);
		}
		measurements.removeAll(toRemove);
		return measurements;
	}


	public static Where<Measurement> selectUploadableWithResultId(int resultId) {
		return Measurement.selectUploadable().and(Measurement_Table.result_id.eq(resultId));
	}

	private static Where<Measurement> selectWithReportId(String report_id) {
		return Measurement.selectUploaded().and(Measurement_Table.report_id.eq(report_id));
	}

	private static Set<String> getReportsUploaded(Context c) {
		Where<Measurement> msmQuery = Measurement.selectUploaded();
		Set<String> reportIds = new HashSet<>();
		List<Measurement> measurements = msmQuery.queryList();
		for (Measurement measurement : measurements){
			if (measurement.hasReportFile(c))
				reportIds.add(measurement.report_id);
		}
		return reportIds;
	}

	private static List<Measurement> selectMeasurementsWithLog(Context c) {
		Where<Measurement> msmQuery = Measurement.selectDone();
		List<Measurement> measurementsLog = new ArrayList<>();
		List<Measurement> measurements = msmQuery.queryList();
		for (Measurement measurement : measurements){
			if (measurement.hasLogFile(c))
				measurementsLog.add(measurement);
		}
		return measurementsLog;
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

	public Boolean hasLogFile(Context c){
		return Measurement.getLogFile(c, this.result.id, this.test_name).exists();
	}

	public static File getLogFile(Context c, int resultId, String test_name) {
		return new File(getMeasurementDir(c), resultId + "_" + test_name + ".log");
	}

	public String getUrlString() {
		if (url == null)
			return null;
		return url.url;
	}

	public void deleteLogFile(Context c){
		try {
			Measurement.getLogFile(c, this.result.id, this.test_name).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteLogFileAfterAWeek(Context c) {
		if (System.currentTimeMillis() - start_time.getTime() > PreferenceManager.DELETE_LOGS_DELAY) {
			deleteLogFile(c);
		}
	}

	public static long getStorageUsed(Context c){
		String database = AppDatabase.NAME + ".db";
		return getFolderSize(getMeasurementDir(c)) +
				c.getDatabasePath(database).length() +
				c.getDatabasePath(database + "-shm").length() +
				c.getDatabasePath(database + "-wal").length();
	}

	private static long getFolderSize(File f) {
		long size = 0;
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				size += getFolderSize(file);
			}
		} else {
			size=f.length();
		}
		return size;
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
				case Signal.NAME:
					test = new Signal();
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
				case Psiphon.NAME:
					test = new Psiphon();
					break;
				case Tor.NAME:
					test = new Tor();
					break;
				case RiseupVPN.NAME:
					test = new RiseupVPN();
					break;
				default:
					test = new Experimental(test_name);
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
		this.testKeys = testKeys;
	}

	public static void deleteUploadedJsons(Application a){
		PreferenceManager pm = a.getPreferenceManager();
		Set<String> reportids = Measurement.getReportsUploaded(a);
		for (String report_id : reportids) {
			a.getApiClient().checkReportId(report_id).enqueue(new CheckReportIdCallback() {
				@Override
				public void onSuccess(Boolean found) {
					if (found)
						Measurement.deleteMeasurementWithReportId(a, report_id);
				}

				@Override
				public void onError(String msg) {
					/* NOTHING */
				}
			});
		}
		pm.setLastCalled();
	}

	public static void deleteMeasurementWithReportId(Context c, String report_id) {
		Where<Measurement> msmQuery = Measurement.selectWithReportId(report_id);
		List<Measurement> measurements = msmQuery.queryList();
		for (int i = 0; i < measurements.size(); i++) {
			Measurement measurement = measurements.get(i);
			measurement.deleteEntryFile(c);
		}
	}

	public static void deleteOldLogs(Application a){
		List<Measurement> measurements = Measurement.selectMeasurementsWithLog(a);
		for (int i = 0; i < measurements.size(); i++) {
			Measurement measurement = measurements.get(i);
			measurement.deleteLogFileAfterAWeek(a);
		}
	}

	public void setReRun(Context c){
		this.deleteEntryFile(c);
		this.deleteLogFile(c);
		this.is_rerun = true;
		this.save();
	}
}
