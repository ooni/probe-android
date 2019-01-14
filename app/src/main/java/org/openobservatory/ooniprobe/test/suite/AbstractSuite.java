package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.io.Serializable;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.annotation.XmlRes;

public abstract class AbstractSuite implements Serializable {
	private int title;
	private int cardDesc;
	private int icon;
	private int color;
	private int themeLight;
	private int themeDark;
	private int desc1;
	private int pref;
	private String anim;
	private String name;
	private String dataUsage;
	private AbstractTest[] testList;
	private Result result;

	public AbstractSuite(String name, @StringRes int title, @StringRes int cardDesc, @DrawableRes int icon, @ColorRes int color, @StyleRes int themeLight, @StyleRes int themeDark, @StringRes int desc1, @XmlRes int pref, String anim, String dataUsage) {
		this.title = title;
		this.cardDesc = cardDesc;
		this.icon = icon;
		this.color = color;
		this.themeLight = themeLight;
		this.themeDark = themeDark;
		this.desc1 = desc1;
		this.pref = pref;
		this.anim = anim;
		this.name = name;
		this.dataUsage = dataUsage;
	}

	@CallSuper public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		return testList;
	}

	public void setTestList(AbstractTest... tests) {
		this.testList = tests;
	}

	public Result getResult() {
		if (result == null)
			result = new Result(name);
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
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

	public int getPref() {
		return pref;
	}

	public String getName() {
		return name;
	}

	public String getDataUsage() {
		return dataUsage;
	}

	public Integer getRuntime(PreferenceManager pm) {
		int runtime = 0;
		for (AbstractTest test : getTestList(pm))
			runtime += test.getRuntime(pm);
		return runtime;
	}
}
