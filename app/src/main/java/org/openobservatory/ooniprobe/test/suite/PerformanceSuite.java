package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.ArrayList;

import androidx.annotation.Nullable;

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
				R.string.Dashboard_Performance_Overview_Paragraph,
				"anim/performance.json",
				"5 - 200 MB");
	}

	@Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null) {
			ArrayList<AbstractTest> list = new ArrayList<>();
			if (pm == null || pm.isRunNdt())
				list.add(new Ndt());
			if (pm == null || pm.isRunDash())
				list.add(new Dash());
			super.setTestList(list.toArray(new AbstractTest[0]));
		}
		return super.getTestList(pm);
	}
}
