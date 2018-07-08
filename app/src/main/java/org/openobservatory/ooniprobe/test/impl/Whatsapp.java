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
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.testKeys.whatsappEndpointsStatus == null || json.testKeys.whatsappWebStatus == null || json.testKeys.registrationServerStatus == null)
			measurement.state = Measurement.State.FAILED;
		else if (json.testKeys.whatsappEndpointsStatus.equals("blocked") || json.testKeys.whatsappWebStatus.equals("blocked") || json.testKeys.registrationServerStatus.equals("blocked")) {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = true;
		}
		super.onEntry(json);
	}
}
