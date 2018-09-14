package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class Dash extends AbstractTest {
	public static final String NAME = "dash";
	public static final String MK_NAME = "Dash";

	public Dash(AbstractActivity activity) {
		super(activity, NAME, MK_NAME, R.string.Test_Dash_Fullname, 0);
		if(!activity.getPreferenceManager().isDashServerAuto()){
			this.settings.options.server = activity.getPreferenceManager().getdashServer();
			this.settings.options.port = activity.getPreferenceManager().getdashServerPort();
		}
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.DashTest(), result, index, testCallback);
	}

	/*
		 onEntry method for dash test, check "failure" key
		 !=null => failed
		 */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		measurement.is_failed = json.test_keys.failure != null;
	}
}
