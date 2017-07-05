package org.openobservatory.ooniprobe.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.openobservatory.ooniprobe.BuildConfig;

import java.util.Locale;

public class NotificationRegister extends FirebaseInstanceIdService {
    private static final String TAG = "NotificationRegister";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        System.out.println("token: " + refreshedToken);
        System.out.println("platform: android");
        System.out.println("software_name ooniprobe-android");
        System.out.println("software_version: " + BuildConfig.VERSION_NAME);
        System.out.println("supported_tests");
        System.out.println("language" + Locale.getDefault().getLanguage());
/*
        OrchestrateClient client = new OrchestrateClient();

        client.platform = "android";
        client.software_name = "ooniprobe-android";
        client.software_version = BuildConfig.VERSION_NAME;
        //client.supported_tests = supported_tests_list;
        //client.network_type = network_type;
        //client.available_bandwidth = [available_bandwidth UTF8String];
        client.device_token = refreshedToken;
        client.language = Locale.getDefault().getLanguage();
*/
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        final String auth_secret_file = "orchestration_secret.json";
        /*
        OrchestrateClient client = new OrchestrateClient();
        client.set_registry_url("https://registry.proteus.test.ooni.io");
        client.increase_verbosity();
        client.increase_verbosity();
        client.set_geoip_country_path("GeoIP.dat");
        client.set_geoip_asn_path("GeoIPASNum.dat");

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
                        if (error.as_bool()) {
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
                */
    }
}
