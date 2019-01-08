package org.openobservatory.ooniprobe.utils;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Locale;
import java.util.TimeZone;

import io.ooni.mk.MKOrchestraClient;

public class NotificationService {
	public static final String WIFI = "wifi";
	public static final String MOBILE = "mobile";
	public static final String NO_INTERNET = "no_internet";

	synchronized public static void sendRegistrationToServer(Application app) {
		if (app.getPreferenceManager().getToken() != null) {
			MKOrchestraClient client = new MKOrchestraClient();
			//client.setAvailableBandwidth(String value); TODO-2.1
			client.setDeviceToken(app.getPreferenceManager().getToken());
			client.setCABundlePath(app.getCacheDir() + "/" + Application.CA_BUNDLE);
			client.setGeoIPCountryPath(app.getCacheDir() + "/" + Application.COUNTRY_MMDB);
			client.setGeoIPASNPath(app.getCacheDir() + "/" + Application.ASN_MMDB);
			client.setLanguage(Locale.getDefault().getLanguage());
			client.setNetworkType(app.getPreferenceManager().getNetworkType());
			client.setPlatform("android");
			//TODO-2.1
			client.setProbeTimezone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
			client.setRegistryURL(BuildConfig.NOTIFICATION_SERVER);
			client.setSecretsFile(app.getFilesDir() + "/orchestration_secret.json");
			client.setSoftwareName("ooniprobe-android");
			client.setSoftwareVersion(VersionUtils.get_software_version());
			for (AbstractSuite suite : TestAsyncTask.SUITES)
				for (AbstractTest test : suite.getTestList(null))
					client.addSupportedTest(test.getName());
			client.setTimeout(17);
			client.sync();
		}
	}
}
