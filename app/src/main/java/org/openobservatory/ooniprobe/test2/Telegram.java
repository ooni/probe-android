package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Telegram extends AbstractTest.TestJsonResult {
	public Telegram(AbstractActivity activity) {
		super(activity, Test.TELEGRAM, new org.openobservatory.measurement_kit.nettests.TelegramTest());
	}

	/*
         if "telegram_http_blocking", "telegram_tcp_blocking", "telegram_web_status" are null => failed
         if either "telegram_http_blocking" or "telegram_tcp_blocking" is true, OR if "telegram_web_status" is "blocked" => anomalous
     */
	@Override public void onEntry(JsonResult json) {
		super.onEntry(json);
		if(json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.telegram_http_blocking == null || keys.telegram_tcp_blocking == null || keys.telegram_web_status == null)
				measurement.state = measurementFailed;
			else if (Boolean.valueOf(keys.telegram_http_blocking) || Boolean.valueOf(keys.telegram_tcp_blocking) || keys.telegram_web_status.equals("blocked"))
				measurement.anomaly = true;

			Summary summary = result.getSummary();
			summary.telegram = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
