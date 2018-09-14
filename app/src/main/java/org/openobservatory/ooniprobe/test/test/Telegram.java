package org.openobservatory.ooniprobe.test.test;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

public class Telegram extends AbstractTest {
	public static final String NAME = "telegram";
	public static final String MK_NAME = "Telegram";

	public Telegram() {
		super(NAME, R.string.Test_Telegram_Fullname, R.drawable.test_telegram);
	}

	@Override public void run(AbstractActivity activity, Result result, int index, TestCallback testCallback) {
		run(activity, new org.openobservatory.measurement_kit.nettests.TelegramTest(), result, index, testCallback);
	}

	/*
         if "telegram_http_blocking", "telegram_tcp_blocking", "telegram_web_status" are null => failed
         if either "telegram_http_blocking" or "telegram_tcp_blocking" is true, OR if "telegram_web_status" is "blocked" => anomalous
     */
	@Override public void onEntry(AbstractActivity activity, @NonNull JsonResult json, Measurement measurement) {
		super.onEntry(activity, json, measurement);
		measurement.is_done = true;
		if (json.test_keys.telegram_http_blocking == null || json.test_keys.telegram_tcp_blocking == null || json.test_keys.telegram_web_status == null)
			measurement.is_failed = true;
		else
			measurement.is_anomaly = json.test_keys.telegram_http_blocking || json.test_keys.telegram_tcp_blocking || json.test_keys.telegram_web_status.equals("blocked");
	}
}
