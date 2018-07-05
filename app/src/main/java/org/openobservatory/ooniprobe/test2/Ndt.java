package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class Ndt extends AbstractTest.TestJsonResult {
	public Ndt(AbstractActivity activity) {
		super(activity, Test.NDT_TEST, new org.openobservatory.measurement_kit.nettests.NdtTest());
	}

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
