package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.NdtTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import java.util.HashMap;
import java.util.Map;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Ndt extends MKNetworkTest {
	public Ndt(Context context) {
		super(context);
		super.name = Test.NDT_TEST;
		super.measurement.name = super.name;
	}

	public void run() {
		super.run();
		runTest();
	}

	public void runTest() {
		NdtTest test = new NdtTest();
		test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
			@Override
			public void callback(String entry) {
				onEntry(entry);
			}
		});
		super.initCommon(test);
		//test.run();
	}

	/*
	 onEntry method for ndt test, check "failure" key
	 !=null => failed
	 */
	public void onEntry(String entry) {
		JsonResult json = super.onEntryCommon(entry);
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
		String[] countries = context.getResources().getStringArray(R.array.countries);
		Map<String, String> map = new HashMap<>();
		for (String country : countries) {
			final String[] fields = country.split("\\|");
			map.put(fields[0], fields[1]);
		}
		return map.get(serverName);
	}
}
