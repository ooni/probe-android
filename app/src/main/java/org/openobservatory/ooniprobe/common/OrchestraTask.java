package org.openobservatory.ooniprobe.common;

import android.os.AsyncTask;

import org.openobservatory.engine.Engine;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Locale;
import java.util.Vector;

public class OrchestraTask extends AsyncTask<Void, Void, Void> {
	private Application app;

	public OrchestraTask(Application app) {
		this.app = app;
	}

	public static void sync(Application app) {
		if (app.getPreferenceManager().getToken() != null) {
			org.openobservatory.engine.OrchestraTask client = Engine.newOrchestraTask(
					BuildConfig.SOFTWARE_NAME,
					BuildConfig.VERSION_NAME,
					getSupportedTests(),
					app.getPreferenceManager().getToken(),
					app.getFilesDir() + "/orchestration_secret.json") ;
			//TODO-COUNTLY ORCHESTRATE
			//client.setAvailableBandwidth(String value);
			//what happens when token is nil? should register anyway with empty string
			boolean okay = Engine.maybeUpdateResources(app);
			if (!okay) {
				Crashlytics.logException(new Exception("MKResourcesManager didn't find resources"));
				return;
			}
			client.setCABundlePath(Engine.getCABundlePath(app));
			client.setGeoIPCountryPath(Engine.getCountryDBPath(app));
			client.setGeoIPASNPath(Engine.getASNDBPath(app));
			client.setLanguage(Locale.getDefault().getLanguage());
			client.setNetworkType(ReachabilityManager.getNetworkType(app));
			client.setPlatform("android");
			//client.setProbeTimezone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
			client.setRegistryURL(BuildConfig.NOTIFICATION_SERVER);
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


	@Override protected Void doInBackground(Void... voids) {
		sync(app);
		return null;
	}
}
