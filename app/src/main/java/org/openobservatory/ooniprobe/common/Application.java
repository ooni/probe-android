package org.openobservatory.ooniprobe.common;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.TestKeys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

@Database(name = "v2", version = 1, foreignKeyConstraintsEnforced = true)
public class Application extends android.app.Application {
	static {
		System.loadLibrary("measurement_kit");
	}

	private PreferenceManager preferenceManager;
	private Gson gson;

	@Override public void onCreate() {
		super.onCreate();
		FlowManager.init(this);
		preferenceManager = new PreferenceManager(this);
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonDeserializer()).registerTypeAdapter(TestKeys.Tampering.class, new TamperingJsonDeserializer()).create();
		CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(preferenceManager.isSendCrash()).build();
		Fabric.with(this, new Crashlytics.Builder().core(core).build());
		FirebaseApp.initializeApp(this);
		if (BuildConfig.DEBUG)
			FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
		copyResources(R.raw.geoipasnum, "GeoIPASNum.dat");
		copyResources(R.raw.geoip, "GeoIP.dat");
		//TODO remove before release
		copyResources(R.raw.global, "global.txt");
	}

	private void copyResources(int id, String filename) {
		try {
			openFileInput(filename).close();
		} catch (FileNotFoundException e) {
			try {
				IOUtils.copyStream(getResources().openRawResource(id), openFileOutput(filename, MODE_PRIVATE), true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	public Gson getGson() {
		return gson;
	}
}
