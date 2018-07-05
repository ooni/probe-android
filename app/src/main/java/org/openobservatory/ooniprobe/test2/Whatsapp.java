package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class Whatsapp extends AbstractTest.TestJsonResult {
	public Whatsapp(AbstractActivity activity) {
		super(activity, Test.WHATSAPP, new org.openobservatory.measurement_kit.nettests.WhatsappTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
