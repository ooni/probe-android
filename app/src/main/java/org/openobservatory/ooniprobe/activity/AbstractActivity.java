package org.openobservatory.ooniprobe.activity;

import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;

public abstract class AbstractActivity extends AppCompatActivity {
	public PreferenceManager getPreferenceManager() {
		return ((Application) getApplication()).getPreferenceManager();
	}

	public Gson getGson() {
		return ((Application) getApplication()).getGson();
	}
}
