package org.openobservatory.ooniprobe.test.impl;

import android.support.annotation.NonNull;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.AbstractTest;

public class Telegram extends AbstractTest {
	public static final String NAME = "telegram";

	public Telegram(AbstractActivity activity, Result result) {
		super(activity, NAME, new org.openobservatory.measurement_kit.nettests.TelegramTest(), result);
	}

	/*
         if "telegram_http_blocking", "telegram_tcp_blocking", "telegram_web_status" are null => failed
         if either "telegram_http_blocking" or "telegram_tcp_blocking" is true, OR if "telegram_web_status" is "blocked" => anomalous
     */
	@Override public void onEntry(@NonNull JsonResult json) {
		if (json.test_keys.telegram_http_blocking == null || json.test_keys.telegram_tcp_blocking == null || json.test_keys.telegram_web_status == null)
			measurement.state = Measurement.State.FAILED;
		else {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = json.test_keys.telegram_http_blocking || json.test_keys.telegram_tcp_blocking || json.test_keys.telegram_web_status.equals("blocked");
		}
		super.onEntry(json);
	}
}
