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

public class WebConnectivity extends AbstractTest {
	public static final String NAME = "web_connectivity";

	public WebConnectivity() {
		super(NAME, R.string.Test_WebConnectivity_Fullname, 0, R.string.urlTestWeb, 5);
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

	/*
 	if the option max_runtime is already set in the option and is not MAX_RUNTIME_DISABLED let's use it
 	else if the input are sets we calculate 5 seconds per input
 	at last we check if max_runtime is enabled, in  that case we use the value in the settings

 	first two cases : test is already running and with options and/or URL s
 	last two cases : get max_runtime saved in the preference
 	*/
	@Override public int getRuntime(PreferenceManager pm) {
		if (getMax_runtime() != null  &&
				getMax_runtime() > PreferenceManager.MAX_RUNTIME_DISABLED)
			return 30 + getMax_runtime();
		else if (getInputs() != null)
			return 30 + getInputs().size() * super.getRuntime(pm);
		else if(pm.isMaxRuntimeEnabled())
			return 30 + pm.getMaxRuntime();
		else
			return PreferenceManager.MAX_RUNTIME_DISABLED;
	}
}
