package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class Dash extends AbstractTest {
	public static final String NAME = "dash";

	public Dash(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.DashTest(), result);
	}

	/*
     onEntry method for dash test, check "failure" key
     !=null => failed
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		measurement.state = json.testKeys.failure == null ? Measurement.State.DONE : Measurement.State.FAILED;
		super.onEntry(json);
	}
}
