package org.openobservatory.ooniprobe.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.openobservatory.ooniprobe.R;

import java.io.Serializable;

public class Test implements Serializable {
	public static final String WEBSITES = "websites";
	public static final String INSTANT_MESSAGING = "instant_messaging";
	public static final String MIDDLE_BOXES = "middle_boxes";
	public static final String PERFORMANCE = "performance";

	//these tests don't have a desc or color, some have an icon, some don't
	public static final String DASH = "dash";
	public static final String HTTP_INVALID_REQUEST_LINE = "http_invalid_request_line";
	public static final String HTTP_HEADER_FIELD_MANIPULATION = "http_header_field_manipulation";
	public static final String WEB_CONNECTIVITY = "web_connectivity";
	public static final String NDT = "ndt";
	public static final String NDT_TEST = "ndt_test";
	public static final String WHATSAPP = "whatsapp";
	public static final String TELEGRAM = "telegram";
	public static final String FACEBOOK_MESSENGER = "facebook_messenger";

	int title;
	int desc;
	int icon;
	int color;

	public Test(@StringRes int title, @StringRes int desc, @DrawableRes int icon, @ColorRes int color) {
		this.title = title;
		this.desc = desc;
		this.icon = icon;
		this.color = color;
	}

	public static Test get(String name) {
		switch (name) {
			case WEBSITES:
				return getWebsiteTest();
			case INSTANT_MESSAGING:
				return getInstantMessaging();
			case MIDDLE_BOXES:
				return getMiddleBoxes();
			case PERFORMANCE:
				return getPerformance();
				/*
			case WEB_CONNECTIVITY:
				return context.getString(R.string.Test_WebConnectivity_Fullname);
			case HTTP_INVALID_REQUEST_LINE:
				return context.getString(R.string.Test_HTTPInvalidRequestLine_Fullname);
			case HTTP_HEADER_FIELD_MANIPULATION:
				return context.getString(R.string.Test_HTTPHeaderFieldManipulation_Fullname);
			case NDT:
				return context.getString(R.string.Test_NDT_Fullname);
			case DASH:
				return context.getString(R.string.Test_Dash_Fullname);
			case WHATSAPP:
				return context.getString(R.string.Test_WhatsApp_Fullname);
			case TELEGRAM:
				return context.getString(R.string.Test_Telegram_Fullname);
			case FACEBOOK_MESSENGER:
				return context.getString(R.string.Test_FacebookMessenger_Fullname);
				*/
			default:
				return null;
		}
	}

	public static Test getWebsiteTest() {
		return new Test(R.string.Test_Websites_Fullname, R.string.Dashboard_Websites_Card_Description, R.drawable.test_websites, R.color.color_indigo6);
	}

	public static Test getInstantMessaging() {
		return new Test(R.string.Test_InstantMessaging_Fullname, R.string.Dashboard_InstantMessaging_Card_Description, R.drawable.test_instant_messaging, R.color.color_cyan6);
	}

	public static Test getMiddleBoxes() {
		return new Test(R.string.Test_Middleboxes_Fullname, R.string.Dashboard_Middleboxes_Card_Description, R.drawable.test_middle_boxes, R.color.color_violet8);
	}

	public static Test getPerformance() {
		return new Test(R.string.Test_Performance_Fullname, R.string.Dashboard_Performance_Card_Description, R.drawable.test_performance, R.color.color_fuchsia6);
	}

	public int getTitle() {
		return title;
	}

	public int getDesc() {
		return desc;
	}

	public int getIcon() {
		return icon;
	}

	public int getColor() {
		return color;
	}
}
