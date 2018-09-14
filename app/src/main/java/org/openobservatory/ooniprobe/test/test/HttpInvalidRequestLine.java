package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class HttpInvalidRequestLine extends AbstractTest {
	public static final String NAME = "http_invalid_request_line";
	public static final String MK_NAME = "HttpInvalidRequestLine";

	public HttpInvalidRequestLine(AbstractActivity activity) {
		super(activity, NAME, MK_NAME, R.string.Test_HTTPInvalidRequestLine_Fullname, 0);
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest(), result, index, testCallback);
	}

	/*
         onEntry method for http invalid request line test, check "tampering" key
         null => failed
         true => anomalous
     */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.tampering == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.tampering.value;
	}
}
