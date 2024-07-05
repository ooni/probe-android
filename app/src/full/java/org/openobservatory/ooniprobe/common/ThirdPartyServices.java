package org.openobservatory.ooniprobe.common;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;
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
            Countly.sharedInstance().consent().removeConsentAll();
            if (app.getPreferenceManager().isNotifications())
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

    public static void addLogExtra(final @NotNull String key, final @NotNull String value){
        if (Sentry.isEnabled())
            Sentry.setExtra(key,value);
    }

    public static boolean shouldShowOnboardingCrash() {
        return true;
    }

    public static void acceptCrash(Application app) {
        app.getPreferenceManager().setSendCrash(true);
    }

    public static void checkUpdates(Activity activity){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Apply a flexible update
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // The current activity making the update request.
                            activity,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                            // Include a request code to later monitor this update request.
                            PreferenceManager.ASK_UPDATE_APP);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
