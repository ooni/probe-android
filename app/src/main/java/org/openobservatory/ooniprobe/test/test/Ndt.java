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
	public static final String MK_NAME = "Ndt";
	private String[] countries;

	public Ndt() {
		super(NAME, MK_NAME, R.string.Test_NDT_Fullname, 0, 45);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		countries = c.getResources().getStringArray(R.array.countries);
		Settings settings = new Settings(c, pm);
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_failed = json.test_keys.failure != null;
		calculateServerName(json.test_keys);
		measurement.setTestKeys(json.test_keys);
	}

	private void calculateServerName(TestKeys keys) {
		String[] parts = keys.server_address.split("\\.");
		if (parts.length > 3) {
			keys.server_name = parts[3];
			keys.server_country = getAirportCountry(parts[3].substring(0, 3));
		}
	}

	private String getAirportCountry(String serverName) {
		for (String country : countries)
			if (country.startsWith(serverName))
				return country.split("\\|")[1];
		return null;
	}
}
