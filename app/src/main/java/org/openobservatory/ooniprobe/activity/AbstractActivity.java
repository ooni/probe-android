package org.openobservatory.ooniprobe.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.OONIOrchestraClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;

import okhttp3.OkHttpClient;

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

	public OONIOrchestraClient getOrchestraClient() {
		return ((Application) getApplication()).getOrchestraClient();
	}

	public OONIAPIClient getApiClient() {
		return ((Application) getApplication()).getApiClient();
	}

	public OkHttpClient getOkHttpClient() {
		return ((Application) getApplication()).getOkHttpClient();
	}
}
