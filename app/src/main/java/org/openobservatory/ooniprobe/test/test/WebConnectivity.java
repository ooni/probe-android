package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";
	public static final String MK_NAME = "WebConnectivity";
	private List<String> inputs;
	private Float max_runtime;

	public WebConnectivity() {
		super(NAME, MK_NAME, R.string.Test_WebConnectivity_Fullname, 0);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		settings.options.max_runtime = max_runtime;
		settings.inputs = inputs;
		run(c, pm, gson, settings, result, index, testCallback);
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public void setMax_runtime(Float max_runtime) {
		this.max_runtime = max_runtime;
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
	}
}
