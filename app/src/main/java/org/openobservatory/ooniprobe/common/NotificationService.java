package org.openobservatory.ooniprobe.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import ly.count.android.sdk.messaging.CountlyPush;

public class NotificationService {
    private static final String TEST_RUN = "TEST_RUN";

    public static void notifyTestEnded(Context c, AbstractSuite testSuite) {
        sendNotification(c, c.getString(R.string.General_AppName), c.getString(testSuite.getTitle()) + " " + c.getString(R.string.Notification_FinishedRunning), c.getResources().getDrawable(testSuite.getIcon()));
    }

    public static void sendNotification(Context c, String title, String message, Drawable icon) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO create other channels for remote notifications
            notificationManager.createNotificationChannel(
                    new NotificationChannel(TEST_RUN, c.getString(R.string.Settings_Notifications_OnTestCompletion), NotificationManager.IMPORTANCE_DEFAULT)
            );
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(c, TEST_RUN);
        b.setAutoCancel(true);
        b.setDefaults(Notification.DEFAULT_ALL);
        if (icon != null){
            Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
            b.setLargeIcon(bitmap);
        }
        b.setSmallIcon(R.drawable.notification_icon);
        b.setContentTitle(title);
        b.setContentText(message);
        b.setContentIntent(PendingIntent.getActivity(c, 0, MainActivity.newIntent(c, R.id.testResults), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(1, b.build());
    }

    public static void setToken(Application a){
        if (a.getPreferenceManager().getToken() != null)
            CountlyPush.onTokenRefresh(a.getPreferenceManager().getToken());
        System.out.println("CountlyPush " + a.getPreferenceManager().getToken());
    }

    public static void setChannel(Context c){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                // Create the NotificationChannel
                NotificationChannel channel = new NotificationChannel(CountlyPush.CHANNEL_ID, "countly_hannel_name", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("countly_channel_description");
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
