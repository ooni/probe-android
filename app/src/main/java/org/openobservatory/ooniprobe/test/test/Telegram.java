package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;

public class Telegram extends AbstractTest {
	public static final String NAME = "telegram";
	public static final String MK_NAME = "Telegram";

	public Telegram(Context c, PreferenceManager pm, Gson gson) {
		super(c, pm, gson, NAME, MK_NAME, R.string.Test_Telegram_Fullname, R.drawable.test_telegram);
	}

	@Override public void run(Result result, int index, TestCallback testCallback) {
		run(new org.openobservatory.measurement_kit.nettests.TelegramTest(), result, index, testCallback);
	}

	@Override public void onEntry(@NonNull JsonResult json, Measurement measurement) {
		super.onEntry(json, measurement);
		measurement.is_done = true;
		if (json.test_keys.telegram_http_blocking == null || json.test_keys.telegram_tcp_blocking == null || json.test_keys.telegram_web_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.telegram_http_blocking || json.test_keys.telegram_tcp_blocking || json.test_keys.telegram_web_status.equals("blocked");
	}
}
