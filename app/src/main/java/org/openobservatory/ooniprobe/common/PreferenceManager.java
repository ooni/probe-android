package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	private static final String SEND_CRASH = "send_crash";
	private SharedPreferences sp;

	public PreferenceManager(Context context) {
		sp = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
	}


	public boolean isSendCrash(){
		return sp.getBoolean(SEND_CRASH, true);
	}
}
