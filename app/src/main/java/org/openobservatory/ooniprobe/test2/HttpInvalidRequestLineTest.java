package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttp;

public class HttpInvalidRequestLineTest extends AbstractTest.TestJsonResultHttp {
	public HttpInvalidRequestLineTest(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest());
	}

	@Override public void onEntry(JsonResultHttp result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
