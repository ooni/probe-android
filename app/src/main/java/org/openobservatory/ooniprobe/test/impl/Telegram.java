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
		if (json.testKeys.telegramHttpBlocking == null || json.testKeys.telegramTcpBlocking == null || json.testKeys.telegramWebStatus == null)
			measurement.state = Measurement.State.FAILED;
		else if (json.testKeys.telegramHttpBlocking || json.testKeys.telegramTcpBlocking || json.testKeys.telegramWebStatus.equals("blocked")) {
			measurement.state = Measurement.State.DONE;
			measurement.anomaly = true;
		}
		super.onEntry(json);
	}
}
