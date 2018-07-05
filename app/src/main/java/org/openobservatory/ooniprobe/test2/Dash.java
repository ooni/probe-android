package org.openobservatory.ooniprobe.test2;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Dash extends AbstractTest.TestJsonResult {
	public Dash(AbstractActivity activity) {
		super(activity, new org.openobservatory.measurement_kit.nettests.DashTest());
		super.name = Test.DASH;
		super.measurement.name = super.name;
		initTest();
	}

	public void initTest(){
		org.openobservatory.measurement_kit.nettests.DashTest test = new org.openobservatory.measurement_kit.nettests.DashTest();
		this.test = test;
		test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
			@Override
			public void callback(String entry) {
				onEntry(entry);
			}
		});
		// ?? super.initCommon();
	}

	/*
     onEntry method for dash test, check "failure" key
     !=null => failed
     */
	public void onEntry(String entry) {
		JsonResult json = super.onEntryCommon(entry);
		if(json != null) {
			JsonResult.TestKeys keys = json.test_keys;
			if (keys.failure != null)
				measurement.state = measurementFailed;

			Summary summary = result.getSummary();
			summary.dash = keys;
			super.updateSummary();
			measurement.save();
		}
	}
}
