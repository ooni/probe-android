package org.openobservatory.ooniprobe.test.test;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.model.settings.Settings;

import androidx.annotation.NonNull;

public class Ndt extends AbstractTest {
	public static final String NAME = "ndt";
	private static final String MK_NAME = "Ndt";

	public Ndt() {
		super(NAME, MK_NAME, R.string.Test_NDT_Fullname, 0, R.string.urlTestNdt, 45);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_failed = json.test_keys.failure != null;
		measurement.setTestKeys(json.test_keys);
		System.out.println("test_keys.server.hostname " + json.test_keys.server.hostname);
		System.out.println("test_keys.server.site " + json.test_keys.server.site);
	}
}
