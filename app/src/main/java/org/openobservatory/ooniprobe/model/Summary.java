package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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

	//Websites

	//Whatsapp

	//Telegram

	//Facebook

	//NDT
	private float getScaledValue(float value) {
		if (value < 100)
			return value;
		else if (value < 100000)
			return value/1000;
		else
			return value/1000000;
	}

	private String setFractionalDigits(float value) {
		if (value < 10)
			String.format(Locale.getDefault(),"%.2f", value);
    	else
			String.format(Locale.getDefault(),"%.1f", value);
	}

	private String getUnit(float value, Context ctx) {
		//We assume there is no Tbit/s (for now!)
		if (value < 100)
			return ctx.getString(R.string.TestResults_Kbps);
    	else if (value < 100000)
			return ctx.getString(R.string.TestResults_Mbps);
    	else
			return ctx.getString(R.string.TestResults_Gbps);
	}

	//Dash
	public String getMedianBitrate(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			float mb = Float.valueOf(testKeys.median_bitrate);
			return setFractionalDigits(getScaledValue(mb));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getMedianBitrateUnit(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			float mb = Float.valueOf(testKeys.median_bitrate);
			return getUnit(mb, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getVideoQuality(Context ctx, Boolean extended){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			float vc = Float.valueOf(testKeys.median_bitrate);
			return minimumBitrateForVideo(vc, extended);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String minimumBitrateForVideo(float videoQuality, Boolean extended){
		if (videoQuality < 600)
			return "240p";
    	else if (videoQuality < 1000)
			return "360p";
    	else if (videoQuality < 2500)
			return "480p";
    	else if (videoQuality < 5000)
			return (extended) ? "720p (HD)" : "720p";
    	else if (videoQuality < 8000)
			return (extended) ? "1080p (full HD)" : "1080p";
    	else if (videoQuality < 16000)
			return (extended) ? "1440p (2k)" : "1440p";
    	else
			return (extended) ? "2160p (4k)" : "2160p";
	}

	public String getPlayoutDelay(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			float md = Float.valueOf(testKeys.min_playout_delay);
			return String.format(Locale.getDefault(),"%.2f", md);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	//HIRL
	public ArrayList getSent(){
		JsonResult.TestKeys testKeys = testKeysMap.get("http_invalid_request_line");
		if (testKeys != null){
			return testKeys.sent;

		}
		return null;
	}

	public ArrayList getReceived(){
		JsonResult.TestKeys testKeys = testKeysMap.get("http_invalid_request_line");
		if (testKeys != null){
			return testKeys.received;

		}
		return null;
	}

}
