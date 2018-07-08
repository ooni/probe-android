package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttpHeader;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class HttpHeaderFieldManipulation extends AbstractTest<JsonResultHttpHeader> {
	public static final String NAME = "http_header_field_manipulation";

	public HttpHeaderFieldManipulation(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest(), JsonResultHttpHeader.class, result);
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
	@Override public void onEntry(@NonNull JsonResultHttpHeader json) {
		JsonResultHttpHeader.TestKeys keys = json.test_keys;
		JsonResultHttpHeader.TestKeys.Tampering tampering = keys.tampering;
		if (tampering == null)
			measurement.state = measurementFailed;
		else if (tampering.isAnomaly())
			measurement.anomaly = true;
		measurement.result.getSummary().testKeysHttpHeader = json.test_keys;
		super.onEntry(json);
	}
}
