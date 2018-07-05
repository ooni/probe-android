package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class HttpInvalidRequestLine extends AbstractTest.TestJsonResult {
	public HttpInvalidRequestLine(AbstractActivity activity) {
		super(activity, Test.HTTP_INVALID_REQUEST_LINE, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest());
	}

	/*
         onEntry method for http invalid request line test, check "tampering" key
         null => failed
         true => anomalous
     */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if(json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.failure != null)
				measurement.state = measurementFailed;
			//TODO
			//else if (Boolean.valueOf(keys.tampering))
			//    measurement.anomaly = true;

			Summary summary = result.getSummary();
			summary.http_invalid_request_line = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
