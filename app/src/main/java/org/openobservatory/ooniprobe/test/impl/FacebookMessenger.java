package org.openobservatory.ooniprobe.test.impl;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class FacebookMessenger extends AbstractTest<JsonResult> {
	public static final String NAME = "facebook_messenger";

	public FacebookMessenger(AbstractActivity activity) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), JsonResult.class);
	}

	/*
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if (json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.facebook_tcp_blocking == null || keys.facebook_dns_blocking == null)
				measurement.state = measurementFailed;
			else if (Boolean.valueOf(keys.facebook_tcp_blocking) || Boolean.valueOf(keys.facebook_dns_blocking))
				measurement.anomaly = true;
			Summary summary = result.getSummary();
			summary.facebook_messenger = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
