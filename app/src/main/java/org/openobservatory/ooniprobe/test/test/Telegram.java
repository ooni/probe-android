package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

public class Telegram extends AbstractTest {
	public static final String NAME = "telegram";
	public static final String MK_NAME = "Telegram";

	public Telegram() {
		super(NAME, MK_NAME, R.string.Test_Telegram_Fullname, R.drawable.test_telegram);
	}

	@Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, TestCallback testCallback) {
		Settings settings = new Settings(c, pm);
		run(c, pm, gson, settings, result, index, testCallback);
	}

	@Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(c, pm, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.telegram_http_blocking == null || json.test_keys.telegram_tcp_blocking == null || json.test_keys.telegram_web_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.telegram_http_blocking || json.test_keys.telegram_tcp_blocking || json.test_keys.telegram_web_status.equals("blocked");
	}
}
