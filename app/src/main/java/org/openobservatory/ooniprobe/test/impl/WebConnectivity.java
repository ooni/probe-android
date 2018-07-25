package org.openobservatory.ooniprobe.test.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";

	public WebConnectivity(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WebConnectivityTest(), result);
	}

	@Override protected void setFilepaths(Context context, BaseTest test) {
		test.set_error_filepath(context.getFilesDir() + measurement.name + "-" + measurement.id + ".log");
	}

	/*
     null => failed {state = FAILED}
     false => not blocked {state = DONE, anomaly = false}
     string (dns, tcp-ip, http-failure, http-diff) => {state = DONE, anomaly = true}
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.test_keys.blocking == null)
			measurement.state = Measurement.State.FAILED;
		else {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = !json.test_keys.blocking.equals("false");
		}
		super.onEntry(json);
	}
}
