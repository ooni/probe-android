package org.openobservatory.ooniprobe.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtility {

    // Register the channel with the system
    public static void setChannel(Context c, String channelID, String channelName,
                                  Boolean vibration, Boolean sound, Boolean lights){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(vibration);
                channel.enableLights(lights);
                if (!sound)
                    channel.setSound(null, null);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
