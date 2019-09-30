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

public class NotificationService {
    private static final String TEST_RUN = "TEST_RUN";

    public static void notifyTestEnded(Context c, AbstractSuite testSuite) {
        sendNotification(c, c.getString(testSuite.getTitle()) + " " + c.getString(R.string.Notification_FinishedRunning), c.getResources().getDrawable(testSuite.getIcon()));
    }

    public static void sendNotification(Context c, String text, Drawable icon) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(new NotificationChannel(TEST_RUN, c.getString(R.string.Settings_Notifications_OnTestCompletion), NotificationManager.IMPORTANCE_DEFAULT));
        NotificationCompat.Builder b = new NotificationCompat.Builder(c, TEST_RUN);
        b.setAutoCancel(true);
        b.setDefaults(Notification.DEFAULT_ALL);
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        b.setLargeIcon(bitmap);
        b.setSmallIcon(R.drawable.notification_icon);
        b.setContentTitle(c.getString(R.string.General_AppName));
        b.setContentText(text);
        b.setContentIntent(PendingIntent.getActivity(c, 0, MainActivity.newIntent(c, R.id.testResults), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(1, b.build());
    }
}
