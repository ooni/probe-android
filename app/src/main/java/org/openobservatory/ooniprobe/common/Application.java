package org.openobservatory.ooniprobe.common;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.apache.commons.io.IOUtils;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.OONIOrchestraClient;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Database(name = "v2", version = 1, foreignKeyConstraintsEnforced = true)
public class Application extends android.app.Application {
	public static final String CA_BUNDLE = "ca_bundle.pem";
	public static final String COUNTRY_MMDB = "country.mmdb";
	public static final String ASN_MMDB = "asn.mmdb";
	private static final int GEO_VER = 2;

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
		copyResources(R.raw.ca_bundle, CA_BUNDLE);
		copyResources(R.raw.asn, ASN_MMDB);
		copyResources(R.raw.country, COUNTRY_MMDB);
	}

	private void copyResources(int id, String filename) {
		File f = new File(getCacheDir(), filename);
		if (!f.exists() || preferenceManager.getGeoVer() != GEO_VER)
			try {
				Log.d(PreferenceManager.GEO_VER, Integer.toString(GEO_VER));
				InputStream input = getResources().openRawResource(id);
				FileOutputStream output = new FileOutputStream(f);
				IOUtils.copy(input, output);
				input.close();
				output.close();
				preferenceManager.setGeoVer(GEO_VER);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
