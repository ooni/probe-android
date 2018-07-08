package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class FacebookMessenger extends AbstractTest<JsonResult> {
	public static final String NAME = "facebook_messenger";

	public FacebookMessenger(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), JsonResult.class, result);
	}

	/*
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		JsonResult.TestKeys keys = json.test_keys;
		if (keys.facebook_tcp_blocking == null || keys.facebook_dns_blocking == null)
			measurement.state = measurementFailed;
		else if (Boolean.valueOf(keys.facebook_tcp_blocking) || Boolean.valueOf(keys.facebook_dns_blocking))
			measurement.anomaly = true;
		measurement.result.getSummary().getTestKeysMap().put(measurement.name, json.test_keys);
		super.onEntry(json);
	}
}
