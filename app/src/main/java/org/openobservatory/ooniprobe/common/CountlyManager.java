package org.openobservatory.ooniprobe.common;

import android.content.Context;

import org.openobservatory.ooniprobe.BuildConfig;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.DeviceId;
import ly.count.android.sdk.messaging.CountlyPush;

public class CountlyManager {
    private static String[] basicFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.location,
    };

    private static String[] analyticsFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.sessions,
            ly.count.android.sdk.Countly.CountlyFeatureNames.views,
            ly.count.android.sdk.Countly.CountlyFeatureNames.events
            //TODO evaluate scrolls, clicks, forms, attribution
    };

    private static String[] crashFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.crashes,
    };

    private static String[] pushFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
    };

    public static void register(Context ctx, PreferenceManager preferenceManager){
        CountlyConfig config = new CountlyConfig()
                .setAppKey("146836f41172f9e3287cab6f2cc347de3f5ddf3b")
                .setContext(ctx)
                //.setDeviceId("TODO")
                .setIdMode(DeviceId.Type.ADVERTISING_ID)
                .setRequiresConsent(true)
                .setConsentEnabled(basicFeatures)
                .setServerURL(BuildConfig.NOTIFICATION_SERVER)
                .setLoggingEnabled(!BuildConfig.DEBUG)
                .setViewTracking(true)
                .setHttpPostForced(true)
                .setViewTracking(true)
                .enableCrashReporting();
        CountlyManager.reloadConsent(preferenceManager);
        Countly.sharedInstance().init(config);
    }

    public static void reloadConsent(PreferenceManager preferenceManager){
        Countly.sharedInstance().consent().setConsent(crashFeatures, preferenceManager.isSendCrash());
        Countly.sharedInstance().consent().setConsent(analyticsFeatures, preferenceManager.isSendAnalytics());
        Countly.sharedInstance().consent().setConsent(pushFeatures, preferenceManager.isNotifications());
    }
}
