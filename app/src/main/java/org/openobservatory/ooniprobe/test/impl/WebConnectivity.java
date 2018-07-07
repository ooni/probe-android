package org.openobservatory.ooniprobe.test.impl;

import android.content.Context;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class WebConnectivity extends AbstractTest<JsonResult> {
	public static final String NAME = "web_connectivity";

	public WebConnectivity(AbstractActivity activity) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WebConnectivityTest(), JsonResult.class);
	}

	@Override public String getOutputFilepath(Context context) {
		return context.getFilesDir() + name + "-" + System.currentTimeMillis() + ".json";
	}

	@Override public String getErrorFilepath(Context context) {
		return context.getFilesDir() + name + "-" + System.currentTimeMillis() + ".log";
	}

	/*
     null => failed
     false => not blocked
     string (dns, tcp-ip, http-failure, http-diff) => anomalous
     */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if (json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.blocking == null)
				measurement.state = measurementFailed;
			else if (!keys.blocking.equals("false"))
				measurement.anomaly = true;
			Summary summary = result.getSummary();
			summary.web_connectivity = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
