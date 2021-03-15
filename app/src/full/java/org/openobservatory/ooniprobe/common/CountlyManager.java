package org.openobservatory.ooniprobe.common;

import android.app.Activity;
import android.content.Context;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.messaging.CountlyPush;

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

    public static String[] crashFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.crashes,
    };

    private static String[] pushFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
    };

    public static void register(Context ctx, PreferenceManager preferenceManager){
        CountlyConfig config = new CountlyConfig()
                .setAppKey(BuildConfig.COUNTLY_KEY)
                .setContext(ctx)
                .setDeviceId(preferenceManager.getOrGenerateUUID4())
                .setRequiresConsent(true)
                .setConsentEnabled(getConsentsEnabled(preferenceManager))
                .setServerURL(BuildConfig.NOTIFICATION_SERVER)
                .setLoggingEnabled(!BuildConfig.DEBUG)
                .setHttpPostForced(true)
                .enableCrashReporting();
        Countly.sharedInstance().init(config);
    }

    public static String[] getConsentsEnabled(PreferenceManager preferenceManager) {
        List<String> consents = new ArrayList(Arrays.asList(basicFeatures));
        if (preferenceManager.isSendAnalytics())
            consents.addAll(Arrays.asList(analyticsFeatures));
        if (preferenceManager.isNotifications())
            consents.addAll(Arrays.asList(pushFeatures));
        return consents.toArray((new String[0]));
    }

    public static void reloadConsent(Context ctx, PreferenceManager preferenceManager){
        FlavorApplication.reloadCrashConsent(ctx, preferenceManager);
        if (Countly.sharedInstance().isInitialized()) {
            Countly.sharedInstance().consent().setConsent(analyticsFeatures, preferenceManager.isSendAnalytics());
            Countly.sharedInstance().consent().setConsent(pushFeatures, preferenceManager.isNotifications());
        }
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

    public static void setToken(String token){
    }

    public static void initPush(Application app){
        CountlyPush.init(app, BuildConfig.DEBUG? Countly.CountlyMessagingMode.TEST:Countly.CountlyMessagingMode.PRODUCTION);
        NotificationService.setChannel(app, CountlyPush.CHANNEL_ID, app.getString(R.string.Settings_Notifications_Label), true, true, true);
        NotificationService.setToken(app);
    }

}