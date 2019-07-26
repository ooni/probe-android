package org.openobservatory.ooniprobe.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.client.ApiClient;
import org.openobservatory.ooniprobe.client.OrchestrateClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;

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

	public OrchestrateClient getOrchestrateClient() {
		return ((Application) getApplication()).getOrchestrateClient();
	}

	public ApiClient getApiClient() {
		return ((Application) getApplication()).getApiClient();
	}
}
