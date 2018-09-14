package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class HttpHeaderFieldManipulation extends AbstractTest {
	public static final String NAME = "http_header_field_manipulation";
	public static final String MK_NAME = "HttpHeaderFieldManipulation";

	public HttpHeaderFieldManipulation() {
		super(NAME, R.string.Test_HTTPHeaderFieldManipulation_Fullname, 0);
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest(), result, index, testCallback);
	}

	/*
         onEntry method for http header field manipulation test, check "failure" key
         null => failed
         true => anomalous
         then the keys in the "tampering" object will be checked, if any of them is not null and TRUE, then test is anomalous
          	tampering {
		        header_field_name
		        header_field_number
		        header_field_value
		        header_name_capitalization
		        request_line_capitalization
		        total
	        }
     */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.failure != null && json.test_keys.tampering == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.tampering.value;
	}
}
