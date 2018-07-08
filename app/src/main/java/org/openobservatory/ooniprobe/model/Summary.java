package org.openobservatory.ooniprobe.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Summary {
	@SerializedName("totalMeasurements")
	public int totalMeasurements;
	@SerializedName("okMeasurements")
	public int okMeasurements;
	@SerializedName("failedMeasurements")
	public int failedMeasurements;
	@SerializedName("anomalousMeasurements")
	public int anomalousMeasurements;
	@SerializedName("testKeysMap")
	private HashMap<String, JsonResult.TestKeys> testKeysMap;

	public static Summary fromJson(String json) {
		return new Gson().fromJson(json, Summary.class);
	}

	public HashMap<String, JsonResult.TestKeys> getTestKeysMap() {
		if (testKeysMap == null)
			testKeysMap = new HashMap<>();
		return testKeysMap;
	}
}
