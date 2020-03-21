package org.openobservatory.ooniprobe.common;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.OONIOrchestraClient;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import java.util.Date;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.DeviceId;
import ly.count.android.sdk.messaging.CountlyPush;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Application extends android.app.Application {

	static {
		System.loadLibrary("measurement_kit");
	}

	private PreferenceManager preferenceManager;
	private Gson gson;
	private boolean testRunning;
	private OkHttpClient okHttpClient;
	private OONIOrchestraClient orchestraClient;
	private OONIAPIClient apiClient;

	@Override public void onCreate() {
		super.onCreate();
		FlowManager.init(this);
		preferenceManager = new PreferenceManager(this);
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateAdapter()).registerTypeAdapter(TestKeys.Tampering.class, new TamperingJsonDeserializer()).create();
		FlavorApplication.onCreate(this, preferenceManager.isSendCrash());
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		if (preferenceManager.canCallDeleteJson())
			Measurement.deleteUploadedJsons(this);

		//NotificationService.setChannel(this);
		// prepare features that should be added to the group
		String[] groupFeatures = new String[]{ Countly.CountlyFeatureNames.sessions, Countly.CountlyFeatureNames.views, Countly.CountlyFeatureNames.crashes, Countly.CountlyFeatureNames.push };

		// create the feature group
		// Countly.sharedInstance().createFeatureGroup("groupName", groupFeatures);
		//TODO disable analytics in debug mode  or use other server
		//Countly.sharedInstance().setRequiresConsent(true);
		CountlyConfig config = new CountlyConfig()
				.setAppKey("fd78482a10e95fd471925399adbcb8ae1a45661f")
				.setContext(this)
				//.setDeviceId(DeviceId.Type.ADVERTISING_ID.toString())
				//.setDeviceId("lorenzo")
				.setDeviceId(null)
				.setIdMode(DeviceId.Type.ADVERTISING_ID)
				//.setIdMode(DeviceId.Type.OPEN_UDID)
				//.setRequiresConsent(true)
				.setConsentEnabled(groupFeatures)
				//.setIdMode(DeviceId.Type.ADVERTISING_ID)
				.setServerURL("https://mia-countly-test.ooni.nu")
				//.setLoggingEnabled(!BuildConfig.DEBUG)
				.setLoggingEnabled(true)
				.setViewTracking(true)
				.setHttpPostForced(true)
				.enableCrashReporting();
		Countly.sharedInstance().init(config);
		CountlyPush.init(this, Countly.CountlyMessagingMode.PRODUCTION);
		NotificationService.setToken(this);
        /*
        Deprecated code
        Countly.sharedInstance().init(this, "https://mia-countly-test.ooni.nu", "fd78482a10e95fd471925399adbcb8ae1a45661f", null, DeviceId.Type.ADVERTISING_ID);
        Countly.sharedInstance().initMessaging(this, MainActivity.class, "951667061699", Countly.CountlyMessagingMode.PRODUCTION);
        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();
        */
	}

	public OkHttpClient getOkHttpClient() {
		if (okHttpClient == null) {
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.level(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
			okHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
		}
		return okHttpClient;
	}

	public OONIOrchestraClient getOrchestraClient() {
		if (orchestraClient == null) {
			orchestraClient = new Retrofit.Builder()
					.baseUrl(BuildConfig.OONI_ORCHESTRATE_BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.client(getOkHttpClient())
					.build().create(OONIOrchestraClient.class);
		}
		return orchestraClient;
	}

	public OONIAPIClient getApiClient() {
		if (apiClient == null) {
			apiClient = new Retrofit.Builder()
					.baseUrl(BuildConfig.OONI_API_BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.client(getOkHttpClient())
					.build().create(OONIAPIClient.class);
		}
		return apiClient;
	}

	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public Gson getGson() {
		return gson;
	}

	public boolean isTestRunning() {
		return testRunning;
	}

	public void setTestRunning(boolean testRunning) {
		this.testRunning = testRunning;
	}
}
