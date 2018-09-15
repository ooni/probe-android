package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.measurement_kit.nettests.WebConnectivityTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";
	public static final String MK_NAME = "WebConnectivitys";

	public WebConnectivity() {
		super(NAME, MK_NAME, R.string.Test_WebConnectivity_Fullname, 0);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		WebConnectivityTest test = new WebConnectivityTest();
		test.set_option("max_runtime", "90");
		test.add_input("http://4genderjustice.org/");
		settings.options.max_runtime = pm.getMaxRuntime();
		settings.inputs = new ArrayList<>(Arrays.asList("http://4genderjustice.org/", "http://www.google.com/"));
		run(c, pm, gson, settings, test, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
		measurement.url = new Url(json.input);
	}
}
