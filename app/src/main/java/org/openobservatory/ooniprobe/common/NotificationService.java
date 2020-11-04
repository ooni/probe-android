package org.openobservatory.ooniprobe.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.messaging.CountlyPush;

public class NotificationService {
    private static final String TEST_RUN = "TEST_RUN";

    public static void initNotification(Application app){
        PreferenceManager preferenceManager = app.getPreferenceManager();
        if (!preferenceManager.isNotifications())
            return;
        CountlyPush.init(app, BuildConfig.DEBUG? Countly.CountlyMessagingMode.TEST:Countly.CountlyMessagingMode.PRODUCTION);
        NotificationService.setChannel(app, CountlyPush.CHANNEL_ID, app.getString(R.string.Settings_Notifications_Label));
        NotificationService.setToken(app);
    }

    /*
     * Used for local notification, ex "test has finished running"
     */
    private static void sendNotification(Context c, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        NotificationCompat.Builder b = new NotificationCompat.Builder(c, TEST_RUN);
        b.setAutoCancel(true);
        b.setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setColor(c.getColor(R.color.color_base));
        } else {
            b.setColor(c.getResources().getColor(R.color.color_base));
        }
        b.setSmallIcon(R.drawable.notification_icon);
        b.setContentTitle(title);
        b.setContentText(message);
        b.setContentIntent(PendingIntent.getActivity(c, 0, MainActivity.newIntent(c, R.id.testResults), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(1, b.build());
    }

    private static void setToken(Application a){
        if (a.getPreferenceManager().getToken() != null)
            CountlyPush.onTokenRefresh(a.getPreferenceManager().getToken());
    }

    // Register the channel with the system
    public static void setChannel(Context c, String channelID, String channelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
