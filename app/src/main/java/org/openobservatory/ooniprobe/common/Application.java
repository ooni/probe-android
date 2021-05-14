package org.openobservatory.ooniprobe.common;

import android.app.ActivityManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.di.AppComponent;
import org.openobservatory.ooniprobe.di.ApplicationModule;
import org.openobservatory.ooniprobe.di.DaggerAppComponent;
import org.openobservatory.ooniprobe.model.database.Measurement;

import javax.inject.Inject;

import okhttp3.OkHttpClient;


public class Application extends android.app.Application {
	@Inject PreferenceManager _preferenceManager;
	@Inject Gson _gson;
	@Inject OkHttpClient _okHttpClient;
	@Inject OONIAPIClient _apiClient;

	protected AppComponent component;

	@Override public void onCreate() {
		super.onCreate();
		component = buildDagger();
		component.inject(this);

		FlowManager.init(this);
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
		ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
		if (_preferenceManager.canCallDeleteJson())
			Measurement.deleteUploadedJsons(this);
		Measurement.deleteOldLogs(this);
		ThirdPartyServices.reloadConsents(this);
	}

	protected AppComponent buildDagger() {
		return DaggerAppComponent.builder().applicationModule(new ApplicationModule(this)).build();
	}

	public OkHttpClient getOkHttpClient() {
		return _okHttpClient;
	}

	public OONIAPIClient getApiClient() {
		return _apiClient;
	}

	public PreferenceManager getPreferenceManager() {
		return _preferenceManager;
	}

	public Gson getGson() {
		return _gson;
	}

	public boolean isTestRunning() {
		return checkServiceRunning(RunTestService.class);
	}

	/**
	 * Check if the service is Running https://stackoverflow.com/a/24729110
	 * @param serviceClass the class of the Service
	 *
	 * @return true if the service is running otherwise false
	 */
	public boolean checkServiceRunning(Class<?> serviceClass){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if (serviceClass.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	//from https://medium.com/mindorks/detecting-when-an-android-app-is-in-foreground-or-background-7a1ff49812d7
	public class AppLifecycleObserver implements LifecycleObserver {

		@OnLifecycleEvent(Lifecycle.Event.ON_START)
		public void onEnterForeground() {
			_preferenceManager.incrementAppOpenCount();
		}

		@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
		public void onEnterBackground() {
		}
	}

}