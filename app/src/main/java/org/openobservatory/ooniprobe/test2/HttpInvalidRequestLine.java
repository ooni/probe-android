package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttp;

public class HttpInvalidRequestLine extends AbstractTest.TestJsonResultHttp {
	public HttpInvalidRequestLine(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest());
	}

	@Override public void onEntry(JsonResultHttp result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
