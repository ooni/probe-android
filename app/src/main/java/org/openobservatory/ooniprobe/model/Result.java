package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Table(database = Application.class)
public class Result extends BaseModel {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public float duration;
	@Column public long dataUsageDown;
	@Column public long dataUsageUp;
	@Column public boolean viewed;
	@Column public boolean done;
	@Column public Date startTime;
	@Column public String name;
	@Column public String ip;
	@Column public String asn;
	@Column public String asnName;
	@Column public String country;
	@Column public String networkName;
	@Column public String networkType;
	@Column public String summary;
	List<Measurement> measurements;
	private Summary summaryObj;

	public Result() {
		this.startTime = new Date();
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	@OneToMany(methods = {OneToMany.Method.ALL}, variableName = "measurements")
	public List<Measurement> getMeasurements() {
		if (measurements == null || measurements.isEmpty())
			measurements = SQLite.select().from(Measurement.class).where(Measurement_Table.result_id.eq(id)).queryList();
		return measurements;
	}

	public String getLocalizedNetworkType(Context context) {
		if (this.networkType.equals("wifi"))
			return context.getString(R.string.TestResults_Summary_Hero_WiFi);
		else if (this.networkType.equals("mobile"))
			return context.getString(R.string.TestResults_Summary_Hero_Mobile);
		else if (this.networkType.equals("no_internet"))
			return context.getString(R.string.TestResults_Summary_Hero_NoInternet);
		return "";
	}

	public void addDuration(double value) {
		this.duration += value;
	}

	public String getFormattedDataUsageUp() {
		return readableFileSize(this.dataUsageUp);
	}

	public String getFormattedDataUsageDown() {
		return readableFileSize(this.dataUsageDown);
	}

	/*
	Three scenarios:
    I'm running the test, I start the empty summary, I add stuff and save
    I'm running the test, there is data in the summary, I add stuff and save
    I have to get the summary of an old test and don't modify it
	*/
	public Summary getSummary() {
		if (this.summaryObj == null) {
			if (this.summary != null)
				this.summaryObj = Summary.fromJson(this.summary);
			else
				this.summaryObj = new Summary();
		}
		return this.summaryObj;
	}

	public void setSummary() {
		this.summary = new Gson().toJson(summary);
	}

	public String getAsn(Context context) {
		if (this.asn != null)
			return this.asn;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public String getAsnName(Context context) {
		if (this.asnName != null)
			return this.asnName;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public String getCountry(Context context) {
		if (this.country != null)
			return this.country;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	@Override public boolean delete() {
		//TODO delete logFile and jsonFile for every measurement and the measurements
		return super.delete();
	}
}
