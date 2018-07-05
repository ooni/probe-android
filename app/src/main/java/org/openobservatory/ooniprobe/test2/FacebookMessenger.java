package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Test;

public class FacebookMessenger extends AbstractTest.TestJsonResult {
	public FacebookMessenger(AbstractActivity activity) {
		super(activity, Test.FACEBOOK_MESSENGER, new org.openobservatory.measurement_kit.nettests.FacebookMessengerTest());
	}

	/*
        if "facebook_tcp_blocking", "facebook_dns_blocking" are null => failed
        if "facebook_tcp_blocking" or "facebook_dns_blocking" are true => anomalous
     */
	/*public void onEntry(String entry) {
		JsonResult json = super.onEntryCommon(entry);
		if(json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.facebook_tcp_blocking == null || keys.facebook_dns_blocking == null)
				measurement.state = measurementFailed;
			else if (Boolean.valueOf(keys.facebook_tcp_blocking) || Boolean.valueOf(keys.facebook_dns_blocking))
				measurement.anomaly = true;

			Summary summary = result.getSummary();
			summary.facebook_messenger = keys;
			super.updateSummary();
			measurement.save();
		}
	}*/

	@Override public void onEntry(JsonResult result) {
		super.onEntry(result);
		// TODO add onEntry specific logic here
	}
}
