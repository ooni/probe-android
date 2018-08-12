package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.impl.Dash;
import org.openobservatory.ooniprobe.test.impl.FacebookMessenger;
import org.openobservatory.ooniprobe.test.impl.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.impl.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.impl.Ndt;
import org.openobservatory.ooniprobe.test.impl.Telegram;
import org.openobservatory.ooniprobe.test.impl.WebConnectivity;
import org.openobservatory.ooniprobe.test.impl.Whatsapp;

import java.util.ArrayList;
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
	@SerializedName("web_connectivity")
	public JsonResult.TestKeys web_connectivity;
	@SerializedName("whatsapp")
	public JsonResult.TestKeys whatsapp;
	@SerializedName("telegram")
	public JsonResult.TestKeys telegram;
	@SerializedName("facebook_messenger")
	public JsonResult.TestKeys facebook_messenger;
	@SerializedName("http_invalid_request_line")
	public JsonResult.TestKeys http_invalid_request_line;
	@SerializedName("http_header_field_manipulation")
	public JsonResult.TestKeys http_header_field_manipulation;
	@SerializedName("ndt")
	public JsonResult.TestKeys ndt;
	@SerializedName("dash")
	public JsonResult.TestKeys dash;

	public void setTestKeys(String name, JsonResult.TestKeys testKeys) {
		switch (name) {
			case WebConnectivity.NAME:
				web_connectivity = testKeys;
				break;
			case Whatsapp.NAME:
				whatsapp = testKeys;
				break;
			case Telegram.NAME:
				telegram = testKeys;
				break;
			case FacebookMessenger.NAME:
				facebook_messenger = testKeys;
				break;
			case HttpInvalidRequestLine.NAME:
				http_invalid_request_line = testKeys;
				break;
			case HttpHeaderFieldManipulation.NAME:
				http_header_field_manipulation = testKeys;
				break;
			case Ndt.NAME:
				ndt = testKeys;
				break;
			case Dash.NAME:
				dash = testKeys;
				break;
		}
	}
	//Websites
	//Whatsapp
	//Telegram
	//Facebook

	//NDT
	private double getScaledValue(double value) {
		if (value < 100)
			return value;
		else if (value < 100000)
			return value / 1000;
		else
			return value / 1000000;
	}

	private String setFractionalDigits(double value) {
		if (value < 10)
			return String.format(Locale.getDefault(), "%.2f", value);
		else
			return String.format(Locale.getDefault(), "%.1f", value);
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
	public String getMedianBitrate(Context ctx) {
		if (dash != null) {
			return setFractionalDigits(getScaledValue(dash.median_bitrate));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getMedianBitrateUnit(Context ctx) {
		if (dash != null) {
			return getUnit(dash.median_bitrate, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getVideoQuality(Context ctx, Boolean extended) {
		if (dash != null) {
			return minimumBitrateForVideo(dash.median_bitrate, extended);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String minimumBitrateForVideo(double videoQuality, Boolean extended) {
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

	public String getPlayoutDelay(Context ctx) {
		if (dash != null) {
			return String.format(Locale.getDefault(), "%.2f", dash.min_playout_delay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	//HIRL
	public ArrayList getSent() {
		if (http_invalid_request_line != null) {
			return http_invalid_request_line.sent;
		}
		return null;
	}

	public ArrayList getReceived() {
		if (http_invalid_request_line != null) {
			return http_invalid_request_line.received;
		}
		return null;
	}
}
