package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttp;

public class HttpHeaderFieldManipulation extends AbstractTest.TestJsonResultHttp {
	public HttpHeaderFieldManipulation(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest());
	}

	@Override public void onEntry(JsonResultHttp result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
