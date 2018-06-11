package org.openobservatory.ooniprobe.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.openobservatory.ooniprobe.R;

import java.io.Serializable;

public class Test implements Serializable {
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

	public static Test getWebsiteTest() {
		return new Test(R.string.Test_Websites_Fullname, R.string.Dashboard_Websites_Card_Description, R.drawable.test_websites, R.color.color_cyan9);
	}

	public static Test getInstantMessaging() {
		return new Test(R.string.Test_InstantMessaging_Fullname, R.string.Dashboard_InstantMessaging_Card_Description, R.drawable.test_instant_messaging, R.color.color_green9);
	}

	public static Test getMiddleBoxes() {
		return new Test(R.string.Test_Middleboxes_Fullname, R.string.Dashboard_Middleboxes_Card_Description, R.drawable.test_middle_boxes, R.color.color_blue9);
	}

	public static Test getPerformance() {
		return new Test(R.string.Test_Performance_Fullname, R.string.Dashboard_Performance_Card_Description, R.drawable.test_performance, R.color.color_yellow9);
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
