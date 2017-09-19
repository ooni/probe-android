package org.openobservatory.ooniprobe.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.swig.Error;
import org.openobservatory.measurement_kit.swig.OrchestrateAuth;
import org.openobservatory.measurement_kit.swig.OrchestrateClient;
import org.openobservatory.measurement_kit.swig.OrchestrateFindLocationCallback;
import org.openobservatory.measurement_kit.swig.OrchestrateRegisterProbeCallback;
import org.openobservatory.measurement_kit.swig.OrchestrateUpdateCallback;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.OONITests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationService {
    private static final String TAG = "NotificationService";

    //TODO handle these warnings
    private static NotificationService instance;
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

    public static NotificationService getInstance(final Context c) {
        if (instance == null) {
            context = c;
            instance = new NotificationService();
            geoip_asn_path = c.getFilesDir() + "/GeoIPASNum.dat";
            geoip_country_path = c.getFilesDir() + "/GeoIP.dat";
            platform = "android";
            software_name = "ooniprobe-android";
            software_version = BuildConfig.VERSION_NAME;
            supported_tests = new ArrayList<>(TestData.getInstance(c, null).availableTests.keySet());
            network_type = getNetworkType(c);
            language = Locale.getDefault().getLanguage();
            if (FirebaseInstanceId.getInstance().getToken() != null)
                device_token = FirebaseInstanceId.getInstance().getToken();
            else
                device_token = null;

            final IntentFilter mIFNetwork = new IntentFilter();
            mIFNetwork.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
            c.registerReceiver(new ConnectionStateMonitor(), mIFNetwork);
        }
        return instance;
    }

    public void setDevice_token(String token){
        device_token = token;
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
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
        client.set_registry_url(OONITests.NOTIFICATION_SERVER);
        //client.set_available_bandwidth();

        client.find_location(
                new OrchestrateFindLocationCallback() {
                    @Override
                    public void callback(
                            final Error error, final String probe_asn,
                            final String probe_cc) {
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
                                    new OrchestrateRegisterProbeCallback() {
                                        @Override
                                        public void callback(
                                                final Error error,
                                                final OrchestrateAuth auth) {
                                            if (error.as_bool()) {
                                                System.out.println(error.reason());
                                                return;
                                            }
                                            Error err = auth.dump(auth_secret_file);
                                            System.out.println(
                                                    "Error: " + err.reason());
                                        }
                                    });
                            return;
                        }
                        client.update(
                                auth,
                                new OrchestrateUpdateCallback() {
                                    @Override
                                    public void callback(
                                            final Error error,
                                            final OrchestrateAuth auth) {
                                        if (error.as_bool()) {
                                            System.out.println(error.reason());
                                            return;
                                        }
                                        Error err = auth.dump(auth_secret_file);
                                        System.out.println("Error: " + err.reason());
                                    }
                                });
                    }
                });
    }

    //https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    public static String getNetworkType(Context context){
        String networkType = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "wifi";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = "mobile";
            }
        } else {
            // not connected to the internet
            networkType = "no_internet";
        }
        //Log.d(TAG, networkType);
        return networkType;
    }

    public void updateNetworkType(Context context) {
        network_type = getNetworkType(context);
        if (!network_type.equals("no_internet"))
            sendRegistrationToServer();
    }

}
