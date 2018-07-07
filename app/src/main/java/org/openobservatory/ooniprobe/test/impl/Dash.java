package org.openobservatory.ooniprobe.test.impl;

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
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if (json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.failure != null)
				measurement.state = measurementFailed;
			super.updateSummary(json);
			measurement.save();
		}
	}
}
