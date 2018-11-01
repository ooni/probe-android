package org.openobservatory.ooniprobe.activity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {
	public PreferenceManager getPreferenceManager() {
		return ((Application) getApplication()).getPreferenceManager();
	}

	public Gson getGson() {
		return ((Application) getApplication()).getGson();
	}

	public ArrayList<String> getCustomUrl() {
		return ((Application) getApplication()).getCustomUrl();
	}

	public boolean isTestRunning() {
		return ((Application) getApplication()).isTestRunning();
	}

	public void setTestRunning(boolean testRunning) {
		((Application) getApplication()).setTestRunning(testRunning);
	}
}
