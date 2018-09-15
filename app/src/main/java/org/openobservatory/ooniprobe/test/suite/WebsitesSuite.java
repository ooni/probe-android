package org.openobservatory.ooniprobe.test.suite;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

public class WebsitesSuite extends AbstractSuite {
	public static final String NAME = "websites";

	public WebsitesSuite() {
		super(NAME,
				R.string.Test_Websites_Fullname,
				R.string.Dashboard_Websites_Card_Description,
				R.drawable.test_websites,
				R.color.color_indigo6,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Websites,
				R.style.Theme_AppCompat_NoActionBar_App_Websites,
				R.string.Dashboard_Websites_Overview_Paragraph_1,
				R.string.Dashboard_Websites_Overview_Paragraph_2,
				R.xml.preferences_websites,
				"anim/websites.json");
	}

	@Override public AbstractTest[] getTestList(Context c, PreferenceManager pm, Gson gson) {
		return new AbstractTest[]{new WebConnectivity(c, pm, gson)};
	}
}
