package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Summary {
	@SerializedName("total")
	public int totalMeasurements;
	@SerializedName("ok")
	public int okMeasurements;
	@SerializedName("failed")
	public int failedMeasurements;
	@SerializedName("anomalous")
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
	private String getUpload(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("ndt");
		if(testKeys != null){
			return setFractionalDigits(getScaledValue(testKeys.simple.upload));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String getUploadUnit(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("ndt");
		if(testKeys != null){
			return getUnit(testKeys.simple.upload, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String getUploadWithUnit(Context ctx){
		String uploadUnit = getUploadUnit(ctx);
		if (!uploadUnit.equals(ctx.getString(R.string.TestResults_NotAvailable)))
			return getUpload(ctx) + " " + uploadUnit;
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String getDownload(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("ndt");
		if(testKeys != null){
			return setFractionalDigits(getScaledValue(testKeys.simple.download));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String getDownloadUnit(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("ndt");
		if(testKeys != null){
			return getUnit(testKeys.simple.download, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String getDownloadWithUnit(Context ctx){
		String downloadUnit = getDownloadUnit(ctx);
		if (!downloadUnit.equals(ctx.getString(R.string.TestResults_NotAvailable)))
			return getDownload(ctx) + " " + downloadUnit;
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private double getScaledValue(double value) {
		if (value < 100)
			return value;
		else if (value < 100000)
			return value/1000;
		else
			return value/1000000;
	}

	private String setFractionalDigits(double value) {
		if (value < 10)
			return String.format(Locale.getDefault(),"%.2f", value);
    	else
			return String.format(Locale.getDefault(),"%.1f", value);
	}

	private String getUnit(double value, Context ctx) {
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
			return setFractionalDigits(getScaledValue(testKeys.median_bitrate));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getMedianBitrateUnit(Context ctx){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			return getUnit(testKeys.median_bitrate, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getVideoQuality(Context ctx, Boolean extended){
		JsonResult.TestKeys testKeys = testKeysMap.get("dash");
		if (testKeys != null){
			return minimumBitrateForVideo(testKeys.median_bitrate, extended);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String minimumBitrateForVideo(double videoQuality, Boolean extended){
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
			return String.format(Locale.getDefault(),"%.2f", testKeys.min_playout_delay);
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
