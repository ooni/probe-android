package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;

public class FacebookMessenger extends AbstractTest {
	public static final String NAME = "facebook_messenger";
	public static final String MK_NAME = "FacebookMessenger";

	public FacebookMessenger(Context c, PreferenceManager pm, Gson gson) {
		super(c, pm, gson, NAME, MK_NAME, R.string.Test_FacebookMessenger_Fullname, R.drawable.test_facebook_messenger);
	}

	@Override public void run(Result result, int index, TestCallback testCallback) {
		run(new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest(), result, index, testCallback);
	}

	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.facebook_tcp_blocking == null || json.test_keys.facebook_dns_blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.facebook_tcp_blocking || json.test_keys.facebook_dns_blocking;
	}
}
