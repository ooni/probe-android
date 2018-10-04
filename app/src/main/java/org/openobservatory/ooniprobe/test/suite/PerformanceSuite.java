package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.ArrayList;

public class PerformanceSuite extends AbstractSuite {
	public static final String NAME = "performance";

	public PerformanceSuite() {
		super(NAME,
				R.string.Test_Performance_Fullname,
				R.string.Dashboard_Performance_Card_Description,
				R.drawable.test_performance,
				R.color.color_fuchsia6,
				R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Performance,
				R.style.Theme_MaterialComponents_NoActionBar_App_Performance,
				R.string.Dashboard_Performance_Overview_Paragraph_1,
				R.string.Dashboard_Performance_Overview_Paragraph_2,
				R.xml.preferences_performance,
				"anim/performance.json");
	}

	@Override public AbstractTest[] getTestList(PreferenceManager pm) {
		ArrayList<AbstractTest> list = new ArrayList<>();
		if (pm.isRunNdt())
			list.add(new Ndt());
		if (pm.isRunDash())
			list.add(new Dash());
		return list.toArray(new AbstractTest[0]);
	}
}
