package org.openobservatory.ooniprobe.test.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class WebConnectivity extends AbstractTest<JsonResult> {
	public static final String NAME = "web_connectivity";

	public WebConnectivity(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WebConnectivityTest(), JsonResult.class, result);
	}

	@Override protected void setFilepaths(Context context, BaseTest test) {
		test.set_error_filepath(context.getFilesDir() + measurement.name + "-" + measurement.id + ".log");
	}

	/*
     null => failed
     false => not blocked
     string (dns, tcp-ip, http-failure, http-diff) => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		JsonResult.TestKeys keys = json.test_keys;
		if (keys.blocking == null)
			measurement.state = measurementFailed;
		else if (!keys.blocking.equals("false"))
			measurement.anomaly = true;
		measurement.result.getSummary().getTestKeysMap().put(measurement.name, json.test_keys);
		super.onEntry(json);
	}
}
