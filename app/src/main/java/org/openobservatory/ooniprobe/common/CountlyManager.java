package org.openobservatory.ooniprobe.common;

import android.content.Context;

import org.openobservatory.ooniprobe.BuildConfig;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.DeviceId;

public class CountlyManager {
    public static String[] basicFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.location,
    };

    public static String[] analyticsFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.sessions,
            ly.count.android.sdk.Countly.CountlyFeatureNames.views,
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
            ly.count.android.sdk.Countly.CountlyFeatureNames.events
            //TODO evaluate scrolls, clicks, forms, attribution
    };

    public static String[] crashFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.crashes,
    };

    public static String[] pushFeatures = new String[]{
            ly.count.android.sdk.Countly.CountlyFeatureNames.push,
    };

    public static void register(Context ctx, PreferenceManager preferenceManager){
        CountlyConfig config = new CountlyConfig()
                .setAppKey("146836f41172f9e3287cab6f2cc347de3f5ddf3b")
                .setContext(ctx)
                //.setDeviceId("lorenzo")
                //.setDeviceId(null)
                //it won't work with fdroid
                .setIdMode(DeviceId.Type.ADVERTISING_ID)
                //.setIdMode(DeviceId.Type.OPEN_UDID)
                //.setRequiresConsent(true)
                .setConsentEnabled(basicFeatures)
                .setServerURL(BuildConfig.NOTIFICATION_SERVER)
                //.setLoggingEnabled(!BuildConfig.DEBUG)
                .setLoggingEnabled(true)
                .setViewTracking(true)
                .setHttpPostForced(true)
                .enableCrashReporting();

        Countly.sharedInstance().init(config);
        CountlyManager.reloadConsent(preferenceManager);
    }

    public static void reloadConsent(PreferenceManager preferenceManager){
        Countly.sharedInstance().consent().setConsent(crashFeatures, preferenceManager.isSendCrash());
        Countly.sharedInstance().consent().setConsent(analyticsFeatures, preferenceManager.isSendAnalytics());
        Countly.sharedInstance().consent().setConsent(pushFeatures, preferenceManager.isNotifications());
        //TODO-COUNTLY init countly notification?
    }
}
