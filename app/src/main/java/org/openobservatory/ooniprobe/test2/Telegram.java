package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class Telegram extends AbstractTest.TestJsonResult {
	public Telegram(AbstractActivity activity) {
		super(activity, Test.TELEGRAM, new org.openobservatory.measurement_kit.nettests.TelegramTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
