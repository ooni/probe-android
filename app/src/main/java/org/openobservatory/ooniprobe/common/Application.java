package org.openobservatory.ooniprobe.common;

import android.app.ActivityManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.OONIOrchestraClient;
import org.openobservatory.ooniprobe.client.OONITestClient;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Application extends android.app.Application {
	private PreferenceManager preferenceManager;
	private Gson gson;
	private OkHttpClient okHttpClient;
	private OONIOrchestraClient orchestraClient;
	private OONIAPIClient apiClient;
	private OONITestClient testClient;

	@Override public void onCreate() {
		super.onCreate();
		FlowManager.init(this);
		preferenceManager = new PreferenceManager(this);
		CountlyManager.register(this, preferenceManager);
		AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
		ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateAdapter()).registerTypeAdapter(TestKeys.Tampering.class, new TamperingJsonDeserializer()).create();
		FlavorApplication.onCreate(this);
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		if (preferenceManager.canCallDeleteJson())
			Measurement.deleteUploadedJsons(this);
		Measurement.deleteOldLogs(this);
	}

	public OkHttpClient getOkHttpClient() {
		if (okHttpClient == null) {
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.level(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
			okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
					.addInterceptor(logging)
					.addInterceptor(new Interceptor() {
						@Override
						public Response intercept(Chain chain) throws IOException {
							Request request = chain.request().newBuilder()
									.addHeader("User-Agent", "ooniprobe-android/" + BuildConfig.VERSION_NAME)
									.addHeader("Connection", "Close")
									.build();
							return chain.proceed(request);
						}
					})
                    .build();
		}
		return okHttpClient;
	}

	public OONIOrchestraClient getOrchestraClientWithUrl(String url) {
		if (orchestraClient == null) {
			orchestraClient = new Retrofit.Builder()
					.baseUrl(url)
					.addConverterFactory(GsonConverterFactory.create())
					.client(getOkHttpClient())
					.build().create(OONIOrchestraClient.class);
		}
		return orchestraClient;
	}

	public OONIOrchestraClient getOrchestraClient() {
		return getOrchestraClientWithUrl(BuildConfig.OONI_ORCHESTRATE_BASE_URL);
	}

	public OONIAPIClient getApiClientWithUrl(String url) {
		if (apiClient == null) {
			apiClient = new Retrofit.Builder()
					.baseUrl(url)
					.addConverterFactory(GsonConverterFactory.create())
					.client(getOkHttpClient())
					.build().create(OONIAPIClient.class);
		}
		return apiClient;
	}

	public OONIAPIClient getApiClient() {
		return getApiClientWithUrl(BuildConfig.OONI_API_BASE_URL);
	}

	public OONITestClient getTestClientWithUrl(String url) {
		if (testClient == null) {
			testClient = new Retrofit.Builder()
					.baseUrl(url)
					.addConverterFactory(GsonConverterFactory.create())
					.client(getOkHttpClient())
					.build().create(OONITestClient.class);
		}
		return testClient;
	}

	public OONITestClient getTestClient() {
		return getTestClientWithUrl(BuildConfig.OONI_TEST_BASE_URL);
	}

	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public Gson getGson() {
		return gson;
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
			preferenceManager.incrementAppOpenCount();
		}

		@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
		public void onEnterBackground() {
		}
	}

}
