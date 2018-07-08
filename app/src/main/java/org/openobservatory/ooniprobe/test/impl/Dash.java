package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Dash extends AbstractTest<JsonResult> {
	public static final String NAME = "dash";

	public Dash(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.DashTest(), JsonResult.class, result);
	}

	/*
     onEntry method for dash test, check "failure" key
     !=null => failed
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		JsonResult.TestKeys keys = json.test_keys;
		if (keys.failure != null)
			measurement.state = measurementFailed;
		measurement.result.getSummary().getTestKeysMap().put(measurement.name, json.test_keys);
		super.onEntry(json);
	}
}
