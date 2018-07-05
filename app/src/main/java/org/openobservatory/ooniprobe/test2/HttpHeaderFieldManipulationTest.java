package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttp;

public class HttpHeaderFieldManipulationTest extends AbstractTest.TestJsonResultHttp {
	public HttpHeaderFieldManipulationTest(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest());
	}

	@Override public void onEntry(JsonResultHttp result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
