package org.openobservatory.ooniprobe.test;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.XmlRes;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.test.impl.Dash;
import org.openobservatory.ooniprobe.test.impl.FacebookMessenger;
import org.openobservatory.ooniprobe.test.impl.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.impl.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.impl.Ndt;
import org.openobservatory.ooniprobe.test.impl.Telegram;
import org.openobservatory.ooniprobe.test.impl.WebConnectivity;
import org.openobservatory.ooniprobe.test.impl.Whatsapp;

import java.io.Serializable;
import java.util.ArrayList;

public class TestSuite implements Serializable {
	public static final String WEBSITES = "websites";
	public static final String INSTANT_MESSAGING = "instant_messaging";
	public static final String MIDDLE_BOXES = "middle_boxes";
	public static final String PERFORMANCE = "performance";
	private int title;
	private int cardDesc;
	private int icon;
	private int color;
	private int themeLight;
	private int themeDark;
	private int desc1;
	private int desc2;
	private int pref;
	private String anim;
	private String name;

	private TestSuite(String name, @StringRes int title, @StringRes int cardDesc, @DrawableRes int icon, @ColorRes int color, @StyleRes int themeLight, @StyleRes int themeDark, @StringRes int desc1, @StringRes int desc2, @XmlRes int pref, String anim) {
		this.title = title;
		this.cardDesc = cardDesc;
		this.icon = icon;
		this.color = color;
		this.themeLight = themeLight;
		this.themeDark = themeDark;
		this.desc1 = desc1;
		this.desc2 = desc2;
		this.pref = pref;
		this.anim = anim;
		this.name = name;
	}

	public static TestSuite getWebsiteTest() {
		return new TestSuite(WEBSITES,
				R.string.Test_Websites_Fullname,
				R.string.Dashboard_Websites_Card_Description,
				R.drawable.test_websites,
				R.color.color_indigo6,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Websites,
				R.style.Theme_AppCompat_NoActionBar_App_Websites,
				R.string.Dashboard_Websites_Overview_Paragraph_1,
				R.string.Dashboard_Websites_Overview_Paragraph_2,
				R.xml.preferences_websites,
				"anim/websites.json"
		);
	}

	public static TestSuite getInstantMessaging() {
		return new TestSuite(INSTANT_MESSAGING,
				R.string.Test_InstantMessaging_Fullname,
				R.string.Dashboard_InstantMessaging_Card_Description,
				R.drawable.test_instant_messaging,
				R.color.color_cyan6,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_InstantMessaging,
				R.style.Theme_AppCompat_NoActionBar_App_InstantMessaging,
				R.string.Dashboard_InstantMessaging_Overview_Paragraph_1,
				R.string.Dashboard_InstantMessaging_Overview_Paragraph_2,
				R.xml.preferences_instant_messaging,
				"anim/instant_messaging.json"
		);
	}

	public static TestSuite getMiddleBoxes() {
		return new TestSuite(MIDDLE_BOXES,
				R.string.Test_Middleboxes_Fullname,
				R.string.Dashboard_Middleboxes_Card_Description,
				R.drawable.test_middle_boxes,
				R.color.color_violet8,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
				R.style.Theme_AppCompat_NoActionBar_App_MiddleBoxes,
				R.string.Dashboard_Middleboxes_Overview_Paragraph_1,
				R.string.Dashboard_Middleboxes_Overview_Paragraph_2,
				R.xml.preferences_middleboxes,
				"anim/middle_boxes.json"
		);
	}

	public static TestSuite getPerformance() {
		return new TestSuite(PERFORMANCE,
				R.string.Test_Performance_Fullname,
				R.string.Dashboard_Performance_Card_Description,
				R.drawable.test_performance,
				R.color.color_fuchsia6,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Performance,
				R.style.Theme_AppCompat_NoActionBar_App_Performance,
				R.string.Dashboard_Performance_Overview_Paragraph_1,
				R.string.Dashboard_Performance_Overview_Paragraph_2,
				R.xml.preferences_performance,
				"anim/performance.json"
		);
	}

	public AbstractTest[] getTestList(AbstractActivity activity) {
		PreferenceManager preferenceManager = activity.getPreferenceManager();
		Result result = new Result(name);
		result.save();
		ArrayList<AbstractTest> list = new ArrayList<>();
		switch (name) {
			case WEBSITES:
				list.add(new WebConnectivity(activity, result));
				break;
			case INSTANT_MESSAGING:
				if (preferenceManager.isTestWhatsapp())
					list.add(new Whatsapp(activity, result));
				if (preferenceManager.isTestTelegram())
					list.add(new Telegram(activity, result));
				if (preferenceManager.isTestFacebookMessenger())
					list.add(new FacebookMessenger(activity, result));
				break;
			case MIDDLE_BOXES:
				if (preferenceManager.isRunHttpHeaderFieldManipulation())
					list.add(new HttpHeaderFieldManipulation(activity, result));
				if (preferenceManager.isRunHttpInvalidRequestLine())
					list.add(new HttpInvalidRequestLine(activity, result));
				break;
			case PERFORMANCE:
				if (preferenceManager.isRunNdt())
					list.add(new Ndt(activity, result));
				if (preferenceManager.isRunDash())
					list.add(new Dash(activity, result));
				break;
		}
		return list.toArray(new AbstractTest[list.size()]);
	}

	public int getTitle() {
		return title;
	}

	public int getCardDesc() {
		return cardDesc;
	}

	public int getIcon() {
		return icon;
	}

	public String getAnim() {
		return anim;
	}

	public int getColor() {
		return color;
	}

	public int getThemeLight() {
		return themeLight;
	}

	public int getThemeDark() {
		return themeDark;
	}

	public int getDesc1() {
		return desc1;
	}

	public int getDesc2() {
		return desc2;
	}

	public int getPref() {
		return pref;
	}
}
