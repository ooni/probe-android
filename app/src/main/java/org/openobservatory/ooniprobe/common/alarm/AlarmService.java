package org.openobservatory.ooniprobe.common.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.openobservatory.ooniprobe.common.Application;

import java.util.Calendar;

/**
 * https://code.tutsplus.com/tutorials/android-fundamentals-scheduling-recurring-tasks--mobile-5788
 */
public class AlarmService {
    public static void setRecurringAlarm(Context context) {
        cancelRecurringAlarm(context);
        String time = ((Application) context).getPreferenceManager().getAutomatedTestingTime();
        String[] separated = time.split(":");
        int hours = Integer.parseInt(separated[0]);
        int minutes = Integer.parseInt(separated[1]);

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(updateTime.getTimeZone());
        updateTime.set(Calendar.HOUR_OF_DAY, hours);
        updateTime.set(Calendar.MINUTE, minutes);
        updateTime.set(Calendar.SECOND, 0);

        Intent alarm = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
                0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringAlarm);
    }

    public static void cancelRecurringAlarm(Context context) {
        Intent alarm = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
                0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        if (alarms!= null) {
            alarms.cancel(recurringAlarm);
        }
    }
}
