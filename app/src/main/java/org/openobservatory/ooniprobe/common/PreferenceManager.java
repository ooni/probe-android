package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.openobservatory.ooniprobe.R;

public class PreferenceManager {
	private static final String SHOW_INTRO = "first_run";
	private SharedPreferences sp;
	private Resources r;

	PreferenceManager(Context context) {
		sp = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
		r = context.getResources();
	}

	public boolean isSendCrash() {
		return sp.getBoolean(r.getString(R.string.send_crash), true);
	}

	public boolean isShowIntro() {
		return sp.getBoolean(SHOW_INTRO, true);
	}

	public void setShowIntro(boolean showIntro) {
		sp.edit().putBoolean(SHOW_INTRO, showIntro).apply();
	}
}
