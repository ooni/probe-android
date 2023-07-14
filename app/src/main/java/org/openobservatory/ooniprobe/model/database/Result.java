package org.openobservatory.ooniprobe.model.database;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.common.AppDatabase;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Table(database = AppDatabase.class)
public class Result extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String test_group_name;
	@Column public Date start_time;
	@Column public boolean is_viewed;
	@Column public boolean is_done;
	@Column public long data_usage_up;
	@Column public long data_usage_down;
	@Column public String failure_msg;

	@ForeignKey(saveForeignKeyModel = true) public Network network;
	private List<Measurement> measurements;

	public Result() {
	}

	public Result(String test_group_name) {
		this.test_group_name = test_group_name;
		this.start_time = new Date();
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		long kbSize = size * 1024;
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(kbSize) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(kbSize / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static Result getLastResult() {
		return SQLite.select().from(Result.class).orderBy(Result_Table.start_time, false).limit(1).querySingle();
	}

	public static Result getLastResult(String test_group_name) {
		return SQLite.select().from(Result.class).where(Result_Table.test_group_name.eq(test_group_name)).orderBy(Result_Table.start_time, false).limit(1).querySingle();
	}

	public static void deleteAll(Context c) {
		try {
			FileUtils.cleanDirectory(Measurement.getMeasurementDir(c));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Delete.tables(Measurement.class, Result.class, Network.class);
		FlowManager.getDatabase(AppDatabase.class).close();
		FlowManager.getDatabase(AppDatabase.class).reset();
	}

	public List<Measurement> getMeasurements() {
		if (measurements == null)
			measurements = SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true)).orderBy(Measurement_Table.id, true).queryList();
		return measurements;
	}

	/*
	 * Sorting measurements:
	 * by is_anomaly and is_failed for Websites
	 * Whatsapp - Telegram - Facebook for Instant Messaging
	 * Ndt - Dash - HIRL - HHFM for Performance
	 * Psiphon - Tor for Circumvention
	 */
	public List<Measurement> getMeasurementsSorted() {
		if (WebsitesSuite.NAME.equals(test_group_name))
			return SQLite.select().from(Measurement.class)
					.where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true))
					.orderBy(Measurement_Table.is_anomaly, false)
					.orderBy(Measurement_Table.is_failed, false)
					.orderBy(Measurement_Table.id, true)
					.queryList();
		measurements = getMeasurements();
		List<Measurement> measurementsSorted = new ArrayList<>();
		String[] testOrder = getTestOrder();
		if (testOrder == null)
			return measurements;
		for (String testName : testOrder) {
			for (Measurement current : measurements) {
				if (current.test_name.equals(testName)) {
					measurementsSorted.add(current);
					break;
				}
			}
		}
		return measurementsSorted;
	}

	private List<Measurement> getAllMeasurements() {
		return SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id)).queryList();
	}

	public Measurement getMeasurement(String name) {
		return SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.test_name.eq(name), Measurement_Table.is_rerun.eq(false)).querySingle();
	}

	public long countTotalMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true)).count();
	}

	@Deprecated
	public long countCompletedMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false)).count();
	}

	public long countOkMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false), Measurement_Table.is_anomaly.eq(false)).count();
	}

	public long countAnomalousMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false), Measurement_Table.is_anomaly.eq(true)).count();
	}

	private Measurement getFirstMeasurement() {
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false))
				.orderBy(Measurement_Table.start_time, true).querySingle();
	}

	private Measurement getLastMeasurement() {
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false))
				.orderBy(Measurement_Table.start_time, false).querySingle();
	}

	public double getRuntime(){
		Measurement first = getFirstMeasurement();
		Measurement last = getLastMeasurement();
		long diffInMs = last.start_time.getTime() - first.start_time.getTime();
		long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
		return diffInSec + last.runtime;
	}

	public String getFormattedDataUsageUp() {
		return readableFileSize(this.data_usage_up);
	}

	public String getFormattedDataUsageDown() {
		return readableFileSize(this.data_usage_down);
	}

	public AbstractSuite getTestSuite() {
		switch (test_group_name) {
			case WebsitesSuite.NAME:
				return new WebsitesSuite(FlowManager.getContext().getResources());
			case InstantMessagingSuite.NAME:
				return new InstantMessagingSuite(FlowManager.getContext().getResources());
			case MiddleBoxesSuite.NAME:
				return new MiddleBoxesSuite(FlowManager.getContext().getResources());
			case PerformanceSuite.NAME:
				return new PerformanceSuite(FlowManager.getContext().getResources());
			case CircumventionSuite.NAME:
				return new CircumventionSuite(FlowManager.getContext().getResources());
			case ExperimentalSuite.NAME:
				return new ExperimentalSuite(FlowManager.getContext().getResources());
			case OONIRunSuite.NAME:
				return new OONIRunSuite(TestDescriptor.Builder.aTestDescriptor().build());
			default:
				return null;
		}
	}

	private String[] getTestOrder() {
		switch (test_group_name) {
			case WebsitesSuite.NAME:
				return new String[]{WebConnectivity.NAME};
			case InstantMessagingSuite.NAME:
				return new String[]{Whatsapp.NAME, Telegram.NAME, FacebookMessenger.NAME, Signal.NAME};
			case MiddleBoxesSuite.NAME:
				return new String[]{HttpInvalidRequestLine.NAME, HttpHeaderFieldManipulation.NAME};
			case PerformanceSuite.NAME:
				return new String[]{Ndt.NAME, Dash.NAME, HttpInvalidRequestLine.NAME, HttpHeaderFieldManipulation.NAME};
			default:
				return null;
		}
	}

	public void delete(Context c) {
		for (Measurement measurement : getAllMeasurements()) {
			measurement.deleteReportFile(c);
			measurement.deleteLogFile(c);
			measurement.delete();
		}
		delete();
		//Network object is deleted after the Result object to avoid constraint fail
		if (this.network != null)
			this.network.delete();
	}
}
