package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class Whatsapp extends AbstractTest {
	public static final String NAME = "whatsapp";

	public Whatsapp(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.WhatsappTest(), result);
	}

	/*
         if "whatsapp_endpoints_status", "whatsapp_web_status", "registration_server" are null => failed
         if "whatsapp_endpoints_status" or "whatsapp_web_status" or "registration_server_status" are "blocked" => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.whatsapp_endpoints_status == null || json.test_keys.whatsapp_web_status == null || json.test_keys.registration_server_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.whatsapp_endpoints_status.equals("blocked") || json.test_keys.whatsapp_web_status.equals("blocked") || json.test_keys.registration_server_status.equals("blocked");
	}
}
