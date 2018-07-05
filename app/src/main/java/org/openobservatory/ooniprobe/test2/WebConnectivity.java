package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class WebConnectivity extends AbstractTest.TestJsonResult {
	public WebConnectivity(AbstractActivity activity) {
		super(activity, Test.WEB_CONNECTIVITY, new org.openobservatory.measurement_kit.nettests.WebConnectivityTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
