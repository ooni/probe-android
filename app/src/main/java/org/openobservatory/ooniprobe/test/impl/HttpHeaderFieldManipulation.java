package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class HttpHeaderFieldManipulation extends AbstractTest {
	public static final String NAME = "http_header_field_manipulation";

	public HttpHeaderFieldManipulation(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest(), result);
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
	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.failure != null && json.test_keys.tampering == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.tampering.value;
	}
}
