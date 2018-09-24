package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

	public List<Measurement> getMeasurements() {
		if (measurements == null)
			measurements = SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id)).queryList();
		return measurements;
	}

	public Measurement getMeasurement(String name) {
		return SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id), Measurement_Table.test_name.eq(name)).querySingle();
	}

	public Measurement getMeasurement() {
		if (measurements == null)
			return SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id)).querySingle();
		else
			return measurements.get(0);
	}

	public long countMeasurement(Boolean anomaly, Boolean failed) {
		ArrayList<SQLOperator> sqlOperators = new ArrayList<>();
		sqlOperators.add(Measurement_Table.result_id.eq(id));
		sqlOperators.add(Measurement_Table.is_done.eq(true));
		if (failed != null)
			sqlOperators.add(Measurement_Table.is_failed.eq(failed));
		if (anomaly != null)
			sqlOperators.add(Measurement_Table.is_anomaly.eq(anomaly));
		return SQLite.selectCountOf().from(Measurement.class).where(sqlOperators.toArray(new SQLOperator[sqlOperators.size()])).count();
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

	@Override public boolean delete() {
		for (Measurement measurement : measurements)
			measurement.delete();
		network.delete();
		return super.delete();
	}
}
