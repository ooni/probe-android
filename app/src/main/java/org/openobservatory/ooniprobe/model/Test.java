package org.openobservatory.ooniprobe.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

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
