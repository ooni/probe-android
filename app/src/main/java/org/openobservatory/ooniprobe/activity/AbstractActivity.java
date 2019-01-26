package org.openobservatory.ooniprobe.activity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {
	public PreferenceManager getPreferenceManager() {
		return ((Application) getApplication()).getPreferenceManager();
	}

	public Gson getGson() {
		return ((Application) getApplication()).getGson();
	}

	boolean isTestRunning() {
		return ((Application) getApplication()).isTestRunning();
	}

	void setTestRunning(boolean testRunning) {
		((Application) getApplication()).setTestRunning(testRunning);
	}
}
