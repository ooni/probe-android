package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;

import java.text.DecimalFormat;
import java.util.Date;

public class Result {
	public int id;
	public String name;
	public Date startTime;
	public float duration;
	public long dataUsageDown;
	public long dataUsageUp;
	public String ip;
	public String asn;
	public String asnName;
	public String country;
	public String networkName;
	public String networkType;
	public String summary;
	public Summary summaryObj;
	public boolean viewed;
	public boolean done;
	public Measurement measurements;

	public Result() {
		this.startTime = new Date();
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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

	public void save() {
	}

	public void deleteObject() {
		//TODO delete logFile and jsonFile for every measurement and the measurements
	}
}
