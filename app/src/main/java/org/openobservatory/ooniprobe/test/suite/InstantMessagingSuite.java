package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.util.ArrayList;

public class InstantMessagingSuite extends AbstractSuite {
	public static final String NAME = "instant_messaging";

	public InstantMessagingSuite() {
		super(NAME,
				R.string.Test_InstantMessaging_Fullname,
				R.string.Dashboard_InstantMessaging_Card_Description,
				R.drawable.test_instant_messaging,
				R.color.color_cyan6,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_InstantMessaging,
				R.style.Theme_AppCompat_NoActionBar_App_InstantMessaging,
				R.string.Dashboard_InstantMessaging_Overview_Paragraph_1,
				R.string.Dashboard_InstantMessaging_Overview_Paragraph_2,
				R.xml.preferences_instant_messaging,
				"anim/instant_messaging.json");
	}

	@Override public AbstractTest[] getTestList(AbstractActivity activity) {
		PreferenceManager preferenceManager = activity.getPreferenceManager();
		ArrayList<AbstractTest> list = new ArrayList<>();
		if (preferenceManager.isTestWhatsapp())
			list.add(new Whatsapp(activity));
		if (preferenceManager.isTestTelegram())
			list.add(new Telegram(activity));
		if (preferenceManager.isTestFacebookMessenger())
			list.add(new FacebookMessenger(activity));
		return list.toArray(new AbstractTest[list.size()]);
	}
}
