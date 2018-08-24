package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;

public class Dash extends AbstractTest {
	public static final String NAME = "dash";

	public Dash() {
		super(NAME, R.string.Test_Dash_Fullname, 0);
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.DashTest(), result, index, testCallback);
	}

	/*
		 onEntry method for dash test, check "failure" key
		 !=null => failed
		 */
	@Override public void onEntry(PreferenceManager preferenceManager, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(preferenceManager, json, measurement);
		measurement.is_done = true;
		measurement.is_failed = json.test_keys.failure != null;
	}
}
