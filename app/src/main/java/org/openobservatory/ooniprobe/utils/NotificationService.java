package org.openobservatory.ooniprobe.utils;

import android.content.Context;
import android.content.IntentFilter;

import com.google.firebase.iid.FirebaseInstanceId;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.swig.Error;
import org.openobservatory.measurement_kit.swig.OrchestrateAuth;
import org.openobservatory.measurement_kit.swig.OrchestrateClient;
import org.openobservatory.ooniprobe.common.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationService {
	public static final String NOTIFICATION_SERVER_DEV = "https://registry.proteus.test.ooni.io";
	private static final String TAG = "NotificationService";
	private static final String NOTIFICATION_SERVER_PROD = "https://registry.proteus.ooni.io";
	private static final String NOTIFICATION_SERVER = NOTIFICATION_SERVER_PROD;
	public static Context context;
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
			geoip_asn_path = c.getCacheDir() + "/" + Application.GEO_IPASNUM;
			geoip_country_path = c.getCacheDir() + "/" + Application.GEO_IP;
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
		final OrchestrateClient client = new OrchestrateClient();
		client.set_verbosity(LogSeverity.LOG_DEBUG);
		client.use_logcat();
		client.set_geoip_country_path(geoip_country_path);
		client.set_geoip_asn_path(geoip_asn_path);
		client.set_platform(platform);
		client.set_software_name(software_name);
		client.set_software_version(software_version);
		client.set_supported_tests(supported_tests);
		client.set_network_type(network_type);
		client.set_language(language);
		client.set_device_token(device_token);
		client.set_registry_url(NOTIFICATION_SERVER);
		//client.set_available_bandwidth();
		client.find_location(
				(error, probe_asn, probe_cc) -> {
					if (error.as_bool()) {
						System.out.println(error.reason());
						return;
					}
					System.out.println("ASN: " + probe_asn);
					System.out.println("CC: " + probe_cc);
					OrchestrateAuth auth = new OrchestrateAuth();
					client.set_probe_asn(probe_asn);
					client.set_probe_cc(probe_cc);
					Error err = auth.load(auth_secret_file);
					if (err.as_bool()) {
						client.register_probe(
								OrchestrateAuth.make_password(),
								(error1, auth1) -> {
									if (error1.as_bool()) {
										System.out.println(error1.reason());
										return;
									}
									Error err1 = auth1.dump(auth_secret_file);
									System.out.println(
											"Error: " + err1.reason());
								});
						return;
					}
					client.update(
							auth,
							(error12, auth12) -> {
								if (error12.as_bool()) {
									System.out.println(error12.reason());
									return;
								}
								Error err12 = auth12.dump(auth_secret_file);
								System.out.println("Error: " + err12.reason());
							});
				});
	}
}
