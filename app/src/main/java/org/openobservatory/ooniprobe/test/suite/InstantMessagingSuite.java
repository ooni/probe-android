package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class InstantMessagingSuite extends AbstractSuite {
	public static final String NAME = "instant_messaging";

	public InstantMessagingSuite() {
		super(NAME,
				R.string.Test_InstantMessaging_Fullname,
				R.string.Dashboard_InstantMessaging_Card_Description,
				R.drawable.test_instant_messaging,
				R.color.color_cyan6,
				R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_InstantMessaging,
				R.style.Theme_MaterialComponents_NoActionBar_App_InstantMessaging,
				R.string.Dashboard_InstantMessaging_Overview_Paragraph,
				R.xml.preferences_instant_messaging,
				"anim/instant_messaging.json",
				"< 1 MB");
	}

	@Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null) {
			ArrayList<AbstractTest> list = new ArrayList<>();
			if (pm == null || pm.isTestWhatsapp())
				list.add(new Whatsapp());
			if (pm == null || pm.isTestTelegram())
				list.add(new Telegram());
			if (pm == null || pm.isTestFacebookMessenger())
				list.add(new FacebookMessenger());
			super.setTestList(list.toArray(new AbstractTest[0]));
		}
		return super.getTestList(pm);
	}
}
