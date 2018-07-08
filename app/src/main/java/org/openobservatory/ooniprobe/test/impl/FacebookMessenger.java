package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class FacebookMessenger extends AbstractTest {
	public static final String NAME = "facebook_messenger";

	public FacebookMessenger(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), result);
	}

	/*
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.testKeys.facebookTcpBlocking == null || json.testKeys.facebookDnsBlocking == null)
			measurement.state = Measurement.State.FAILED;
		else {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = json.testKeys.facebookTcpBlocking || json.testKeys.facebookDnsBlocking;
		}
		super.onEntry(json);
	}
}
