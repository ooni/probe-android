package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

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
		JsonResult.TestKeys keys = json.test_keys;
		if (keys.failure != null)
			measurement.state = measurementFailed;
		calculateServerName(keys);
		super.onEntry(json);
	}

	private void calculateServerName(JsonResult.TestKeys keys) {
		String serverAddress = keys.server_address;
		String[] parts = serverAddress.split(",");
		if (parts.length > 3) {
			String serverName = parts[3];
			keys.server_name = serverName;
			keys.server_country = getAirportCountry(serverName.substring(0, 3));
		}
	}

	private String getAirportCountry(String serverName) {
		for (String country : countries)
			if (country.startsWith(serverName))
				return country.split("\\|")[1];
		return null;
	}
}
