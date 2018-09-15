package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;

public class HttpInvalidRequestLine extends AbstractTest {
	public static final String NAME = "http_invalid_request_line";
	public static final String MK_NAME = "HttpInvalidRequestLine";

	public HttpInvalidRequestLine(Context c, PreferenceManager pm, Gson gson) {
		super(c, pm, gson, NAME, MK_NAME, R.string.Test_HTTPInvalidRequestLine_Fullname, 0);
	}

	@Override public void run(Result result, int index, TestCallback testCallback) {
		run(new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest(), result, index, testCallback);
	}

	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.tampering == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.tampering.value;
	}
}
