package org.openobservatory.ooniprobe.test.test;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import androidx.annotation.NonNull;

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";
	private static final String MK_NAME = "WebConnectivity";

	public WebConnectivity() {
		super(NAME, MK_NAME, R.string.Test_WebConnectivity_Fullname, 0, R.string.urlTestWeb, 5);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		if (json.test_keys.blocking == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = !json.test_keys.blocking.equals("false");
	}

	@Override public int getRuntime(PreferenceManager pm) {
		if (getMax_runtime() != null  &&
				!getMax_runtime().equals(PreferenceManager.MAX_RUNTIME_DISABLED))
			return 30 + getMax_runtime();
		else if (getInputs() != null)
			return 30 + getInputs().size() * super.getRuntime(pm);
		else if(pm.isMaxRuntimeEnabled())
			return 30 + pm.getMaxRuntime();
		else
			return PreferenceManager.MAX_RUNTIME_DISABLED;
	}
}
