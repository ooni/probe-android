package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class Ndt extends AbstractTest {
	public static final String NAME = "ndt_test";
	private String[] countries;

	public Ndt(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.NdtTest(), result);
		countries = activity.getResources().getStringArray(R.array.countries);
	}

	/*
	 	onEntry method for ndt test, check "failure" key
	 	!=null => failed
	 */
	@Override public void onEntry(@NonNull JsonResult json) {
		measurement.state = json.testKeys.failure == null ? Measurement.State.DONE : Measurement.State.FAILED;
		calculateServerName(json.testKeys);
		super.onEntry(json);
	}

	private void calculateServerName(JsonResult.TestKeys keys) {
		String[] parts = keys.serverAddress.split(",");
		if (parts.length > 3) {
			keys.serverName = parts[3];
			keys.serverCountry = getAirportCountry(parts[3].substring(0, 3));
		}
	}

	private String getAirportCountry(String serverName) {
		for (String country : countries)
			if (country.startsWith(serverName))
				return country.split("\\|")[1];
		return null;
	}
}
