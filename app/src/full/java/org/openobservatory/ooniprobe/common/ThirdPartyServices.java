package org.openobservatory.ooniprobe.common;

import com.google.firebase.FirebaseApp;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

import io.sentry.Sentry;
import io.sentry.android.core.SentryAndroid;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.messaging.CountlyPush;

public class ThirdPartyServices {

    private static String[] pushFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.location,
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
    };

    public static void initCountly(Application app) {
        if (!app.getPreferenceManager().isNotifications())
            return;
        FirebaseApp.initializeApp(app);
        CountlyConfig config = new CountlyConfig()
                .setAppKey(BuildConfig.COUNTLY_KEY)
                .setContext(app)
                .setDeviceId(app.getPreferenceManager().getOrGenerateUUID4())
                .setRequiresConsent(true)
                .setConsentEnabled(pushFeatures)
                .setServerURL(BuildConfig.NOTIFICATION_SERVER)
                .setLoggingEnabled(!BuildConfig.DEBUG)
                .setHttpPostForced(true);
        Countly.sharedInstance().init(config);
        registerPush(app);
    }

    public static void registerPush(Application app){
        if (Countly.sharedInstance().isInitialized()) {
            CountlyPush.init(app, BuildConfig.DEBUG ? Countly.CountlyMessagingMode.TEST : Countly.CountlyMessagingMode.PRODUCTION);
            NotificationUtility.setChannel(app, CountlyPush.CHANNEL_ID, app.getString(R.string.Settings_Notifications_Label), true, true, true);
            ThirdPartyServices.setToken(app);
        }
    }

    public static void initSentry(Application app){
        if (!app.getPreferenceManager().isSendCrash())
            return;
        SentryAndroid.init(app,
                options -> {
                    options.setDsn("https://9dcd83d9519844188803aa817cdcd416@o155150.ingest.sentry.io/5619989");
                    options.setBeforeSend(
                            (event, hint) -> {
                                // Drop an event altogether:
                                if (!app.getPreferenceManager().isSendCrash()) {
                                    return null;
                                }
                                return event;
                            });
                });
    }


    /* We can't shutdown the SDKs during the app lifecycle so we remove consent(s) if any setting changes
     * In case of Sentry the consent ad it's handled in the options.setBeforeSend
     * we don't init the SDK if we don't have crash consent
     */
    public static void reloadConsents(Application app) {
        reloadNotificationConsent(app);
        initSentry(app);
    }

    private static void reloadNotificationConsent(Application app){
        if (Countly.sharedInstance().isInitialized()) {
            Countly.sharedInstance().consent().setConsent(pushFeatures, app.getPreferenceManager().isNotifications());
        }
        else initCountly(app);
    }

    public static void setToken(Application app){
        if (app.getPreferenceManager().getToken() != null)
            CountlyPush.onTokenRefresh(app.getPreferenceManager().getToken());
    }

    public static void logException(Exception e){
        if (Sentry.isEnabled())
            Sentry.captureException(e);
    }

}
