package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class HttpInvalidRequestLine extends AbstractTest {
	public static final String NAME = "http_invalid_request_line";

	public HttpInvalidRequestLine(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest(), result);
	}

	/*
         onEntry method for http invalid request line test, check "tampering" key
         null => failed
         true => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.test_keys.failure != null || json.test_keys.tampering == null) // TODO lorenzo check "|| keys.tampering == null"
			measurement.state = Measurement.State.FAILED;
		else {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = json.test_keys.tampering.value;
		}
		super.onEntry(json);
	}
}
