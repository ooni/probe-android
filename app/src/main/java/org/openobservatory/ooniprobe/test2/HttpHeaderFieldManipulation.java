package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResultHttpHeader;
import org.openobservatory.ooniprobe.model.Test;

public class HttpHeaderFieldManipulation extends AbstractTest.TestJsonResultHttpHeader {
	public HttpHeaderFieldManipulation(AbstractActivity activity) {
		super(activity, Test.HTTP_HEADER_FIELD_MANIPULATION, new org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest());
	}

	@Override public void onEntry(JsonResultHttpHeader result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
