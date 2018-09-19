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
		settings.options.max_runtime = pm.getMaxRuntime();
		settings.inputs = new ArrayList<>(Arrays.asList("http://4genderjustice.org/", "http://www.google.com/"));
		//TODO refactor into downloadUrl function
		for (int i = 0; i < settings.inputs.size(); i++){
			Url url = Url.checkExistingUrl(settings.inputs.get(i).toString());
		}
		run(c, pm, gson, settings, result, index, testCallback);
	}

	public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback, ArrayList urls) {
		Settings settings = new Settings(c, pm);
		settings.options.max_runtime = pm.getMaxRuntime();
		settings.inputs = urls;
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
	}
}
