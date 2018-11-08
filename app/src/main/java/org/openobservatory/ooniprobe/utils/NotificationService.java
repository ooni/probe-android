package org.openobservatory.ooniprobe.utils;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.ooni.mk.MKOrchestraClient;

public class NotificationService {
	public static final String NOTIFICATION_SERVER_DEV = "https://registry.proteus.test.ooni.io";
	private static final String TAG = "NotificationService";
	private static final String NOTIFICATION_SERVER_PROD = "https://registry.proteus.ooni.io";
	private static final String NOTIFICATION_SERVER = NOTIFICATION_SERVER_DEV;
	public static Context context;
	static String ca_bundle_path;
	static String geoip_country_path;
	static String geoip_asn_path;
	static String platform;
	static String software_name;
	static String software_version;
	static List<String> supported_tests;
	static String network_type;
	static String available_bandwidth;
	static String device_token;
	static String language;
	//TODO-ALE refector class
	private static NotificationService instance;

	public static NotificationService getInstance(final Context c) {
		if (instance == null) {
			context = c;
			instance = new NotificationService();
			ca_bundle_path = c.getCacheDir() + "/" + Application.CA_BUNDLE;
			geoip_asn_path = c.getCacheDir() + "/" + Application.ASN_MMDB;
			geoip_country_path = c.getCacheDir() + "/" + Application.COUNTRY_MMDB;
			platform = "android";
			software_name = "ooniprobe-android";
			software_version = VersionUtils.get_software_version();
			supported_tests = new ArrayList<>();
			network_type = ConnectionState.getInstance(c).getNetworkType();
			language = Locale.getDefault().getLanguage();
			if (FirebaseInstanceId.getInstance().getToken() != null)
				device_token = FirebaseInstanceId.getInstance().getToken();
			else
				device_token = null;
		}
		return instance;
	}

	public void setDevice_token(String token) {
		device_token = token;
	}

	/**
	 * Persist token to third-party servers.
	 * <p>
	 * Modify this method to associate the user's FCM InstanceID token with any server-side account
	 * maintained by your application.
	 */
	public void sendRegistrationToServer() {
		final String auth_secret_file = context.getFilesDir() + "/orchestration_secret.json";
		//LOGGING
        /*
        System.out.println("probe_cc: " + geoip_country_path);
        System.out.println("probe_asn: " + geoip_asn_path);
        System.out.println("platform: " + platform);
        System.out.println("software_name: " + software_name);
        System.out.println("software_version: " + software_version);
        System.out.println("supported_tests: " + supported_tests);
        System.out.println("token: " + device_token);
        */
		//if device_token is null the user hasn't enabled push notifications
		if (device_token == null) return;
		MKOrchestraClient client = new MKOrchestraClient();
		//TODO-2.1
		//client.setAvailableBandwidth(String value);
		client.setDeviceToken(device_token);
		client.setCABundlePath(ca_bundle_path);
		client.setGeoIPCountryPath(geoip_country_path);
		client.setGeoIPASNPath(geoip_asn_path);
		client.setLanguage(language);
		client.setNetworkType(network_type);
		client.setPlatform("android");
		client.setProbeTimezone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
		client.setRegistryURL(NOTIFICATION_SERVER);
		client.setSecretsFile(auth_secret_file);
		client.setSoftwareName(software_name);
		client.setSoftwareVersion(software_version);
		for (AbstractSuite suite : TestAsyncTask.SUITES)
			for (AbstractTest test : suite.getTestList(null))
				client.addSupportedTest(test.getName());
		client.setTimeout(17);
		client.sync();
	}
}
