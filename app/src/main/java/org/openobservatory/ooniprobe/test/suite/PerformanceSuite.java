package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.ArrayList;

public class PerformanceSuite extends AbstractSuite {
	public static final String NAME = "performance";

	public PerformanceSuite() {
		super(NAME,
			R.string.Test_Performance_Fullname,
			R.string.Dashboard_Performance_Card_Description,
			R.drawable.test_performance,
			R.drawable.test_performance_24,
			R.color.color_fuchsia6,
			R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Performance,
			R.style.Theme_MaterialComponents_NoActionBar_App_Performance,
			R.string.Dashboard_Performance_Overview_Paragraph_Updated,
			"anim/performance.json",
			R.string.performance_datausage);
	}

	public static PerformanceSuite initForAutoRun() {
		PerformanceSuite suite = new PerformanceSuite();
		suite.setAutoRun(true);
		return suite;
	}

	@Override
	public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null) {
			ArrayList<AbstractTest> list = new ArrayList<>();
			// if ((pm == null || pm.isRunNdt())  && !getAutoRun())
				list.add(new Ndt());
			// if ((pm == null || pm.isRunDash())  && !getAutoRun())
				list.add(new Dash());
			// if ((pm == null || pm.isRunHttpHeaderFieldManipulation()))
				list.add(new HttpHeaderFieldManipulation());
			// if ((pm == null || pm.isRunHttpInvalidRequestLine()))
				list.add(new HttpInvalidRequestLine());
			super.setTestList(Lists.transform(list, test -> {
				if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
				return test;
			}).toArray(new AbstractTest[0]));
		}
		return super.getTestList(pm);
	}
}
