package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	private static final String SEND_CRASH = "send_crash";
	private static final String SHOW_INTRO = "show_intro";
	private SharedPreferences sp;

	PreferenceManager(Context context) {
		sp = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean isSendCrash() {
		return sp.getBoolean(SEND_CRASH, true);
	}

	public boolean isShowIntro() {
		return sp.getBoolean(SHOW_INTRO, true);
	}

	public void setShowIntro(boolean showIntro) {
		sp.edit().putBoolean(SHOW_INTRO, showIntro).apply();
	}
}
