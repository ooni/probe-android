package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class FacebookMessenger extends AbstractTest {
	public static final String NAME = "facebook_messenger";
	public static final String MK_NAME = "FacebookMessenger";

	public FacebookMessenger(AbstractActivity activity) {
		super(activity, NAME, MK_NAME, R.string.Test_FacebookMessenger_Fullname, R.drawable.test_facebook_messenger);
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), result, index, testCallback);
	}

	/*	if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
		if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.facebook_tcp_blocking == null || json.test_keys.facebook_dns_blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.facebook_tcp_blocking || json.test_keys.facebook_dns_blocking;
	}
}
