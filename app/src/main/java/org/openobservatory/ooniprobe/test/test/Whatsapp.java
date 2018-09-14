package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class Whatsapp extends AbstractTest {
	public static final String NAME = "whatsapp";
	public static final String MK_NAME = "Whatsapp";

	public Whatsapp(AbstractActivity activity) {
		super(activity, NAME, R.string.Test_WhatsApp_Fullname, R.drawable.test_whatsapp);
		this.settings.name = MK_NAME;
		if(activity.getPreferenceManager().isTestWhatsappExtensive())
			this.settings.options.all_endpoints = 1;

	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.WhatsappTest(), result, index, testCallback);
	}

	/*
         if "whatsapp_endpoints_status", "whatsapp_web_status", "registration_server" are null => failed
         if "whatsapp_endpoints_status" or "whatsapp_web_status" or "registration_server_status" are "blocked" => anomalous
     */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.whatsapp_endpoints_status == null || json.test_keys.whatsapp_web_status == null || json.test_keys.registration_server_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.whatsapp_endpoints_status.equals("blocked") || json.test_keys.whatsapp_web_status.equals("blocked") || json.test_keys.registration_server_status.equals("blocked");
	}
}
