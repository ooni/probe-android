package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Locale;
import java.util.Vector;

import io.ooni.mk.MKOrchestraTask;

public class OrchestraTask extends AsyncTask<Void, Void, Void> {
	public static final String WIFI = "wifi";
	public static final String MOBILE = "mobile";
	public static final String NO_INTERNET = "no_internet";
	private Application app;

	public OrchestraTask(Application app) {
		this.app = app;
	}

	public static void sync(Application app) {
		if (app.getPreferenceManager().getToken() != null) {
			MKOrchestraTask client = new MKOrchestraTask(
					app.getString(R.string.software_name),
					BuildConfig.VERSION_NAME,
					getSupportedTests(),
					app.getPreferenceManager().getToken(),
					app.getFilesDir() + "/orchestration_secret.json") ;
			//TODO ORCHESTRATE
			//client.setAvailableBandwidth(String value);
			//what happens when token is nil? should register anyway with empry string
			client.setCABundlePath(app.getCacheDir() + "/" + Application.CA_BUNDLE);
			client.setGeoIPCountryPath(app.getCacheDir() + "/" + Application.COUNTRY_MMDB);
			client.setGeoIPASNPath(app.getCacheDir() + "/" + Application.ASN_MMDB);
			client.setLanguage(Locale.getDefault().getLanguage());
			client.setNetworkType(getNetworkType(app));
			client.setPlatform("android");
			//TODO ORCHESTRATE - TIMEZONE
			//client.setProbeTimezone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
			client.setRegistryURL(BuildConfig.NOTIFICATION_SERVER);
			client.setSecretsFile(app.getFilesDir() + "/orchestration_secret.json");
			client.setSoftwareVersion(BuildConfig.VERSION_NAME);
			client.setTimeout(app.getResources().getInteger(R.integer.default_timeout));
			client.updateOrRegister();
		}
	}

	public static Vector<String> getSupportedTests() {
		Vector<String> supportedTest = new Vector<>();
		for (AbstractSuite suite : TestAsyncTask.SUITES)
			for (AbstractTest test : suite.getTestList(null))
				supportedTest.add(test.getName());
		return supportedTest;
	}

	public static String getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null)
			return NO_INTERNET;
		else {
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI)
				return WIFI;
			else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE)
				return MOBILE;
			else
				return NO_INTERNET;
		}
	}

	@Override protected Void doInBackground(Void... voids) {
		sync(app);
		return null;
	}
}
