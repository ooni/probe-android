package org.openobservatory.ooniprobe.test.suite;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
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
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Performance,
				R.style.Theme_AppCompat_NoActionBar_App_Performance,
				R.string.Dashboard_Performance_Overview_Paragraph_1,
				R.string.Dashboard_Performance_Overview_Paragraph_2,
				R.xml.preferences_performance,
				"anim/performance.json");
	}

	@Override public AbstractTest[] getTestList(Context c, PreferenceManager pm, Gson gson) {
		ArrayList<AbstractTest> list = new ArrayList<>();
		if (pm.isRunNdt())
			list.add(new Ndt(c,pm, gson));
		if (pm.isRunDash())
			list.add(new Dash(c, pm, gson));
		return list.toArray(new AbstractTest[list.size()]);
	}
}
