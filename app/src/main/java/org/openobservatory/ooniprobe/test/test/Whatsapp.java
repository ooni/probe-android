package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;

public class Whatsapp extends AbstractTest {
	public static final String NAME = "whatsapp";
	public static final String MK_NAME = "Whatsapp";

	public Whatsapp(Context c, PreferenceManager pm, Gson gson) {
		super(c, pm, gson, NAME, MK_NAME, R.string.Test_WhatsApp_Fullname, R.drawable.test_whatsapp);
		if (pm.isTestWhatsappExtensive())
			this.settings.options.all_endpoints = 1;
	}

	@Override public void run(Result result, int index, TestCallback testCallback) {
		run(new org.openobservatory.measurement_kit.nettests.WhatsappTest(), result, index, testCallback);
	}

	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.whatsapp_endpoints_status == null || json.test_keys.whatsapp_web_status == null || json.test_keys.registration_server_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.whatsapp_endpoints_status.equals("blocked") || json.test_keys.whatsapp_web_status.equals("blocked") || json.test_keys.registration_server_status.equals("blocked");
	}
}
