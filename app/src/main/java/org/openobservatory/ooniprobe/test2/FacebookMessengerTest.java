package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;

public class FacebookMessengerTest extends AbstractTest.TestJsonResult {
	public FacebookMessengerTest(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
