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
     null => failed
     false => not blocked
     string (dns, tcp-ip, http-failure, http-diff) => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.testKeys.blocking == null)
			measurement.state = Measurement.State.FAILED;
		else if (json.testKeys.blocking) {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = true;
		}
		super.onEntry(json);
	}
}
