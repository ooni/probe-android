package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class HttpInvalidRequestLine extends AbstractTest.TestJsonResult {
	public HttpInvalidRequestLine(AbstractActivity activity) {
		super(activity, Test.HTTP_INVALID_REQUEST_LINE, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
