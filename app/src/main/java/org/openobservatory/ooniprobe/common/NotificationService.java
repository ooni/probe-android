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

import ly.count.android.sdk.messaging.CountlyPush;

public class NotificationService {
    private static final String TEST_RUN = "TEST_RUN";

    public static void notifyTestEnded(Context c, AbstractSuite testSuite) {
        setChannel(c, TEST_RUN, c.getString(R.string.Settings_Notifications_OnTestCompletion));
        sendNotification(c, c.getString(R.string.General_AppName), c.getString(testSuite.getTitle()) + " " + c.getString(R.string.Notification_FinishedRunning), testSuite.getIcon());
    }
/*
    public static void notificationReceived(Context c, String title, String message, String type) {
        setChannel(c, TEST_RUN, c.getString(R.string.Settings_Notifications_OnTestCompletion));
        sendNotification(c, c.getString(R.string.General_AppName), c.getString(testSuite.getTitle()) + " " + c.getString(R.string.Notification_FinishedRunning), testSuite.getIcon());
    }
*/

    //TODO icon not used
    public static void sendNotification(Context c, String title, String message, int icon) {
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
        //b.setLargeIcon(BitmapFactory.decodeResource(c.getResources(), icon));
        b.setSmallIcon(R.drawable.notification_icon);

        //TODO maybe set the icon of the test color
        //TODO decide if we want small or big icon of test type
        /*
        Drawable icona = c.getResources().getDrawable(icon);
        if (icona != null){
            Bitmap bitmap = Bitmap.createBitmap(icona.getIntrinsicWidth(), icona.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            icona.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icona.draw(canvas);
            b.setLargeIcon(bitmap);
        }
        */
        b.setContentTitle(title);
        b.setContentText(message);
        //TODO edit intent based on  notification type
        b.setContentIntent(PendingIntent.getActivity(c, 0, MainActivity.newIntent(c, R.id.testResults), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(1, b.build());
    }

    public static void setToken(Application a){
        if (a.getPreferenceManager().getToken() != null)
            CountlyPush.onTokenRefresh(a.getPreferenceManager().getToken());
        System.out.println("CountlyPush " + a.getPreferenceManager().getToken());
    }

    //TODO better code https://developer.android.com/training/notify-user/channels
    //https://code.tutsplus.com/tutorials/android-o-how-to-use-notification-channels--cms-28616
    //https://support.count.ly/hc/en-us/articles/360037754031-Android-SDK
    //Creating an existing notification channel with its original values performs no operation, so it's safe to call this code when starting an app.
    public static void setChannel(Context c, String channelID, String channelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(
                        new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                );
            }
        }
    }

}
