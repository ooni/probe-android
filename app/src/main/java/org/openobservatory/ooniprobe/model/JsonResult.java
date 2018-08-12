package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class JsonResult {
	@SerializedName("probe_asn")
	public String probe_asn;
	@SerializedName("probe_cc")
	public String probe_cc;
	@SerializedName("test_start_time")
	public Date test_start_time;
	@SerializedName("measurement_start_time")
	public Date measurement_start_time;
	@SerializedName("test_runtime")
	public Double test_runtime;
	@SerializedName("probe_ip")
	public String probe_ip;
	@SerializedName("report_id")
	public String report_id;
	@SerializedName("input")
	public String input;
	@SerializedName("test_keys")
	public TestKeys test_keys;
}