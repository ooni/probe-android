package org.openobservatory.ooniprobe.test.impl;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.test.AbstractTest;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Whatsapp extends AbstractTest<JsonResult> {
	public static final String NAME = "whatsapp";

	public Whatsapp(AbstractActivity activity) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WhatsappTest(), JsonResult.class);
	}

	/*
         if "whatsapp_endpoints_status", "whatsapp_web_status", "registration_server" are null => failed
         if "whatsapp_endpoints_status" or "whatsapp_web_status" or "registration_server_status" are "blocked" => anomalous
     */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if (json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.whatsapp_endpoints_status == null || keys.whatsapp_web_status == null || keys.registration_server_status == null)
				measurement.state = measurementFailed;
			else if (keys.whatsapp_endpoints_status.equals("blocked") || keys.whatsapp_web_status.equals("blocked") || keys.registration_server_status.equals("blocked"))
				measurement.anomaly = true;
			Summary summary = result.getSummary();
			summary.whatsapp = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
