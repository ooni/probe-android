package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import androidx.annotation.Nullable;

public class WebsitesSuite extends AbstractSuite {
	public static final String NAME = "websites";

	public WebsitesSuite() {
		super(NAME,
				R.string.Test_Websites_Fullname,
				R.string.Dashboard_Websites_Card_Description,
				R.drawable.test_websites,
				R.color.color_indigo6,
				R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Websites,
				R.style.Theme_MaterialComponents_NoActionBar_App_Websites,
				R.string.Dashboard_Websites_Overview_Paragraph,
				R.xml.preferences_websites,
				"anim/websites.json",
				"~ 8 MB", 30);
	}

	@Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null)
			super.setTestList(new WebConnectivity());
		return super.getTestList(pm);
	}

	@Override public Integer getRuntime(@Nullable PreferenceManager pm) {
		return super.getRuntime(pm) + pm.getMaxRuntime();
	}
}
