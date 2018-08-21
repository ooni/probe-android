package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Url;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";

	public WebConnectivity(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WebConnectivityTest(), result);
		test.set_option("max_runtime", preferenceManager.getMaxRuntime());
		test.add_input("http://4genderjustice.org/");
	}

	/*
     null => failed {state = FAILED}
     false => not blocked {state = DONE, anomaly = false}
     string (dns, tcp-ip, http-failure, http-diff) => {state = DONE, anomaly = true}
     */
	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
		measurement.url = new Url(json.input);
	}
}
