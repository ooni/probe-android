package org.openobservatory.ooniprobe.model.database;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Table(database = Application.class)
public class Result extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String test_group_name;
	@Column public Date start_time;
	@Column public double runtime;
	@Column public boolean is_viewed;
	@Column public boolean is_done;
	@Column public long data_usage_up;
	@Column public long data_usage_down;
	@ForeignKey(saveForeignKeyModel = true, deleteForeignKeyModel = true) public Network network;
	// TODO log_file_path
	protected List<Measurement> measurements;

	public Result() {
	}

	public Result(String test_group_name) {
		this.test_group_name = test_group_name;
		this.start_time = new Date();
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static void deleteAll(Context c) {
		try {
			FileUtils.cleanDirectory(c.getFilesDir());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Delete.tables(Measurement.class, Result.class);
	}

	public List<Measurement> getMeasurements() {
		//TODO  AND is_rerun = 0 AND is_done = 1
		if (measurements == null)
			measurements = SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id)).queryList();
		return measurements;
	}

	public Measurement getMeasurement(String name) {
		//TODO  AND is_rerun = 0
		return SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.test_name.eq(name)).querySingle();
	}

	public long countTotalMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true)).count();
	}

	public long countCompletedMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false)).count();
	}

	public long countOkMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false), Measurement_Table.is_anomaly.eq(false)).count();
	}

	public long countAnomalousMeasurements() {
		return SQLite.selectCountOf().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.is_rerun.eq(false), Measurement_Table.is_done.eq(true), Measurement_Table.is_failed.eq(false), Measurement_Table.is_anomaly.eq(true)).count();
	}

	public void addDuration(double value) {
		this.runtime += value;
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
				return new WebsitesSuite();
			case InstantMessagingSuite.NAME:
				return new InstantMessagingSuite();
			case MiddleBoxesSuite.NAME:
				return new MiddleBoxesSuite();
			case PerformanceSuite.NAME:
				return new PerformanceSuite();
			default:
				return null;
		}
	}

	public boolean delete(Context c) {
		for (Measurement measurement : measurements) {
			try {
				new File(c.getFilesDir(), Measurement.getEntryFileName(measurement.id, measurement.test_name)).delete();
				new File(c.getFilesDir(), Measurement.getLogFileName(id, measurement.test_name)).delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			measurement.delete();
		}
		network.delete();
		return delete();
	}
}
