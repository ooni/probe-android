package org.openobservatory.ooniprobe.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;


/**
 * https://code.tutsplus.com/tutorials/android-fundamentals-scheduling-recurring-tasks--mobile-5788
 */

public class NotificationHandler extends FirebaseMessagingService {

    private static final String TAG = "NotificationHandler";
    /*
    Example to execute tasks:
    https://firebase.google.com/docs/cloud-messaging/android/client
    https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/AndroidManifest.xml#L32-L37
    https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseMessagingService.java

    long running task:
    https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyJobService.java
    */

    public static void notifyTestEnded(Context c, String text) {
            sendNotification(c, TestUtility.getTestName(c, text) + " " + c.getString(R.string.finished_running));
        }

        public static void sendNotification(Context c, String text) {

        int icon = R.drawable.notification_icon;

        Intent intent = new Intent(c, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(c);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.ooni_icon))
                .setSmallIcon(icon)
                .setTicker(c.getString(R.string.General_AppName))
                .setContentTitle(c.getString(R.string.General_AppName))
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }
}

