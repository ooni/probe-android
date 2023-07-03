package org.openobservatory.ooniprobe.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.service.ConnectivityChangeService;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.di.ActivityComponent;
import org.openobservatory.ooniprobe.di.AppComponent;
import org.openobservatory.ooniprobe.di.ApplicationModule;
import org.openobservatory.ooniprobe.di.DaggerAppComponent;
import org.openobservatory.ooniprobe.di.FragmentComponent;
import org.openobservatory.ooniprobe.di.ServiceComponent;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import okhttp3.OkHttpClient;


public class Application extends android.app.Application {
	@Inject PreferenceManager _preferenceManager;
	@Inject Gson _gson;
	@Inject OkHttpClient _okHttpClient;
	@Inject OONIAPIClient _apiClient;
	ExecutorService executorService = Executors.newFixedThreadPool(4);

	public AppComponent component;
	@Inject AppLogger logger;

	@Override public void onCreate() {
		super.onCreate();
		component = buildDagger();
		component.inject(this);

		FlowManager.init(this);
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
		ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
		Measurement.deleteOldLogs(this);
		ThirdPartyServices.reloadConsents(this);

		executorService.execute(() -> {
			if (_preferenceManager.canCallDeleteJson())
				Measurement.deleteUploadedJsons(Application.this);
			Measurement.deleteOldLogs(Application.this);
		});
		ThirdPartyServices.reloadConsents(Application.this);
		if (_preferenceManager.isAutomaticallyRunTestOnNetworkChange()){
			ServiceUtil.scheduleConnectivityChangeService(this);
		}
		LocaleUtils.setLocale(new Locale(_preferenceManager.getSettingsLanguage()));
		LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
	}

	protected AppComponent buildDagger() {
		return DaggerAppComponent.builder().applicationModule(new ApplicationModule(this)).build();
	}

	public OONICheckInConfig getOONICheckInConfig() {

		BatteryManager batteryManager = (BatteryManager) this.getSystemService(Context.BATTERY_SERVICE);
		boolean workingOnWifi = ReachabilityManager.getNetworkType(this).equals(ReachabilityManager.WIFI);
		boolean phoneCharging = false;
		String[] categories = getPreferenceManager().getEnabledCategoryArr().toArray(new String[0]);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			phoneCharging = batteryManager.isCharging();
		}

        return new OONICheckInConfig(
                BuildConfig.SOFTWARE_NAME,
                BuildConfig.VERSION_NAME,
                workingOnWifi,
                phoneCharging,
                categories
        );
	}

	public AppComponent getComponent() { return component; }

	public ServiceComponent getServiceComponent() { return component.serviceComponent(); }

	public FragmentComponent getFragmentComponent() { return component.fragmentComponent(); }

	public ActivityComponent getActivityComponent() { return component.activityComponent(); }

	public OkHttpClient getOkHttpClient() {
		return _okHttpClient;
	}

	public OONIAPIClient getApiClient() {
		return _apiClient;
	}

	public PreferenceManager getPreferenceManager() {
		return _preferenceManager;
	}

	public AppLogger getLogger() {
		return logger;
	}

	public Gson getGson() {
		return _gson;
	}

	public boolean isTestRunning() {
		return checkServiceRunning(RunTestService.class);
	}

	/**
	 * Open phone VPN settings.
	 */
	public void openVPNSettings() {
		Intent intent = new Intent("android.net.vpn.SETTINGS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
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

	}

}
