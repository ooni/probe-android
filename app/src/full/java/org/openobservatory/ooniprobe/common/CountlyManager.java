package org.openobservatory.ooniprobe.common;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.sentry.Sentry;
import io.sentry.android.core.SentryAndroid;
import io.sentry.protocol.App;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.messaging.CountlyPush;

//TODO rename in
public class CountlyManager {
    private static String[] basicFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.location,
    };

    public static String[] analyticsFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.sessions,
            ly.count.android.sdk.Countly.CountlyFeatureNames.views,
            ly.count.android.sdk.Countly.CountlyFeatureNames.events
            //TODO evaluate scrolls, clicks, forms, attribution
    };

    //TODO evaluate if countly crash would be needed
    public static String[] crashFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.crashes,
    };

    private static String[] pushFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
    };

    public static void register(Application app){
        //TODO register only if push are enabled
        CountlyConfig config = new CountlyConfig()
                .setAppKey(BuildConfig.COUNTLY_KEY)
                .setContext(app)
                .setDeviceId(app.getPreferenceManager().getOrGenerateUUID4())
                .setRequiresConsent(true)
                .setConsentEnabled(getConsentsEnabled(app.getPreferenceManager()))
                .setServerURL(BuildConfig.NOTIFICATION_SERVER)
                .setLoggingEnabled(!BuildConfig.DEBUG)
                .setHttpPostForced(true)
                .enableCrashReporting();
        Countly.sharedInstance().init(config);
        //TODO init this class will init countly and sentry and push, if the relative settings are on
        //reloadConsent method can be unique
    }

    public static void initPush(Application app){
        if (Countly.sharedInstance().isInitialized()) {
            CountlyPush.init(app, BuildConfig.DEBUG ? Countly.CountlyMessagingMode.TEST : Countly.CountlyMessagingMode.PRODUCTION);
            NotificationService.setChannel(app, CountlyPush.CHANNEL_ID, app.getString(R.string.Settings_Notifications_Label), true, true, true);
            CountlyManager.setToken(app);
        }
    }

    public static String[] getConsentsEnabled(PreferenceManager preferenceManager) {
        //TODO unique array with basic and push. they go together
        List<String> consents = new ArrayList(Arrays.asList(basicFeatures));
        if (preferenceManager.isSendAnalytics())
            consents.addAll(Arrays.asList(analyticsFeatures));
        if (preferenceManager.isNotifications())
            consents.addAll(Arrays.asList(pushFeatures));
        return consents.toArray((new String[0]));
    }

    public static void reloadConsent(Application app){
        reloadCrashConsent(app);
        if (Countly.sharedInstance().isInitialized()) {
            Countly.sharedInstance().consent().setConsent(analyticsFeatures, app.getPreferenceManager().isSendAnalytics());
            Countly.sharedInstance().consent().setConsent(pushFeatures, app.getPreferenceManager().isNotifications());
        }
        else register(app);
    }

    public static void recordEvent(String title) {
        if (Countly.sharedInstance().isInitialized())
            Countly.sharedInstance().events().recordEvent(title);
    }

    public static void recordEvent(String title, HashMap<String, Object> segmentation) {
        if (Countly.sharedInstance().isInitialized())
            Countly.sharedInstance().events().recordEvent(title, segmentation);
    }

    public static void recordView(String title) {
        if (Countly.sharedInstance().isInitialized())
        Countly.sharedInstance().views().recordView(title);
    }

    public static void onStart(Activity activity){
        if (Countly.sharedInstance().isInitialized())
            Countly.sharedInstance().onStart(activity);
    }

    public static void onStop(){
        if (Countly.sharedInstance().isInitialized())
            Countly.sharedInstance().onStop();
    }

    public static void setToken(Application app){
        if (app.getPreferenceManager().getToken() != null)
            CountlyPush.onTokenRefresh(app.getPreferenceManager().getToken());
    }

    public static void reloadCrashConsent(Application app) {
        if (Sentry. isEnabled() && app.getPreferenceManager().isSendCrash())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(app.getPreferenceManager().isSendCrash());
        else
	        initSentry(app);
    }

	public static void initSentry(Application app){
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
}
