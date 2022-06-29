package org.openobservatory.ooniprobe.test.test;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.model.settings.Settings;

public class Ndt extends AbstractTest {
	public static final String NAME = "ndt";
	private String[] countries;

	public Ndt() {
		super(NAME, R.string.Test_NDT_Fullname, 0, R.string.urlTestNdt, 45);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		countries = c.getResources().getStringArray(R.array.countries);
		Settings settings = new Settings(c, pm, isAutoRun());
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_failed = json.test_keys.failure != null;
		calculateServerName(json.test_keys);
		measurement.setTestKeys(json.test_keys);
	}

	private void calculateServerName(TestKeys keys) {
		for (String country : countries) {
			if (keys.server.site != null && country.startsWith(keys.server.site.substring(0, 3))) {
				keys.server_name = keys.server.site;
				keys.server_country = getAirportCountry(keys.server.site.substring(0, 3));
			}
		}
	}

	private String getAirportCountry(String serverName) {
		for (String country : countries)
			if (country.startsWith(serverName))
				return country.split("\\|")[1];
		return null;
	}
}
