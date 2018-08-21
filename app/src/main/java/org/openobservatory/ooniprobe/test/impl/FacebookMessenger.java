package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class FacebookMessenger extends AbstractTest {
	public static final String NAME = "facebook_messenger";

	public FacebookMessenger(AbstractActivity activity, Result result) {
		super(activity, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), result, NAME, R.string.Test_FacebookMessenger_Fullname);
	}

	/*
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.facebook_tcp_blocking == null || json.test_keys.facebook_dns_blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.facebook_tcp_blocking || json.test_keys.facebook_dns_blocking;
	}
}
