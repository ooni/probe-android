package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Ndt extends AbstractTest<JsonResult> {
	private String[] countries;


	public Ndt(AbstractActivity activity) {
		super(activity, Test.NDT_TEST, new org.openobservatory.measurement_kit.nettests.NdtTest(), JsonResult.class);
		countries = activity.getResources().getStringArray(R.array.countries);
	}

	/*
	 	onEntry method for ndt test, check "failure" key
	 	!=null => failed
	 */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if (json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.failure != null)
				measurement.state = measurementFailed;
			Summary summary = result.getSummary();
			summary.ndt = calculateServerName(keys);
			super.updateSummary();
			measurement.save();
		}
	}

	private JsonResult.TestKeys calculateServerName(JsonResult.TestKeys keys) {
		String serverAddress = keys.server_address;
		String[] parts = serverAddress.split(",");
		if (parts.length > 3) {
			String serverName = parts[3];
			keys.server_name = serverName;
			keys.server_country = getAirportCountry(serverName.substring(0, 3));
		}
		return keys;
	}

	private String getAirportCountry(String serverName) {
		for (String country : countries)
			if (country.startsWith(serverName))
				return country.split("\\|")[1];
		return null;
	}
}
