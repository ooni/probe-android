package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.JsonResultHttpHeader;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class HttpHeaderFieldManipulation extends AbstractTest.TestJsonResultHttpHeader {
	public HttpHeaderFieldManipulation(AbstractActivity activity) {
		super(activity, Test.HTTP_HEADER_FIELD_MANIPULATION, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest());
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
	@Override public void onEntry(JsonResultHttpHeader json) {
		super.onEntry(json);
		if(json != null) {
			JsonResultHttpHeader.TestKeys keys = json.test_keys;
			JsonResultHttpHeader.TestKeys.Tampering tampering = keys.tampering;
			if (tampering == null)
				measurement.state = measurementFailed;
			else if (Boolean.valueOf(tampering.header_field_name) ||
					Boolean.valueOf(tampering.header_field_number) ||
					Boolean.valueOf(tampering.header_field_value) ||
					Boolean.valueOf(tampering.header_name_capitalization) ||
					Boolean.valueOf(tampering.request_line_capitalization) ||
					Boolean.valueOf(tampering.total))
				measurement.anomaly = true;

			Summary summary = result.getSummary();
			summary.http_header_field_manipulation = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
