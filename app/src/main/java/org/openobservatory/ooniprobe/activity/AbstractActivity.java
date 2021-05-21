package org.openobservatory.ooniprobe.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.di.ActivityComponent;
import org.openobservatory.ooniprobe.di.AppComponent;

import okhttp3.OkHttpClient;

public abstract class AbstractActivity extends AppCompatActivity {

	public Application getApp() {
		return ((Application) getApplication());
	}

	public AppComponent getComponent() {
		return getApp().getComponent();
	}

	public ActivityComponent getActivityComponent() {
		return getApp().getActivityComponent();
	}

	@Deprecated
	public PreferenceManager getPreferenceManager() {
		return ((Application) getApplication()).getPreferenceManager();
	}

	@Deprecated
	public Gson getGson() {
		return ((Application) getApplication()).getGson();
	}

	boolean isTestRunning() {
		return ((Application) getApplication()).isTestRunning();
	}

	@Deprecated
	public OONIAPIClient getApiClient() {
		return ((Application) getApplication()).getApiClient();
	}

	@Deprecated
	public OkHttpClient getOkHttpClient() {
		return ((Application) getApplication()).getOkHttpClient();
	}
}
