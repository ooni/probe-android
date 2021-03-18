package org.openobservatory.ooniprobe.common.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationUtility;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

public class RunTestService extends Service {
    public static final String CHANNEL_ID = "RunTestService";
    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_INTERRUPT = "interrupt_test";
    private final IBinder mBinder = new TestBinder();
    public TestAsyncTask task;
    public NotificationCompat.Builder builder;
    public NotificationManagerCompat notificationManager;
    ActionReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(ACTION_INTERRUPT);
        receiver = new ActionReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<AbstractSuite> testSuites = (ArrayList<AbstractSuite>) intent.getSerializableExtra("testSuites");
        if (testSuites == null || testSuites.size() == 0)
            return 0;
        Application app = ((Application)getApplication());
        NotificationUtility.setChannel(getApplicationContext(), CHANNEL_ID, app.getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
        Intent notificationIntent = new Intent(this, RunningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getApplication().getString(R.string.Dashboard_Running_Running))
                .setContentText(getApplication().getString(R.string.Dashboard_Running_PreparingTest))
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setProgress(100, 0, false)
                .build();

        task = (TestAsyncTask) new TestAsyncTask(app, testSuites, this).execute();
        //This intent is used to manage the stop test button in the notification
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RunTestService.ACTION_INTERRUPT);
        PendingIntent pIntent = PendingIntent.getBroadcast(this,1, broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(0, getApplicationContext().getString(R.string.Notification_StopTest), pIntent);
        startForeground(NOTIFICATION_ID, builder.build());
        /*
        START_NOT_STICKY says that, after returning from onStartCreated(),
        if the process is killed with no remaining start commands to deliver,
        then the service will be stopped instead of restarted.
        This makes a lot more sense for services that are intended to only run while executing commands sent to them.
        For example, a service may be started every 15 minutes from an alarm to poll some network state.
        If it gets killed while doing that work, it would be best to just let it be stopped and get started the next time the alarm fires.
         */
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shouldShowNotification(this)){
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, MainActivity.newIntent(getApplicationContext(), R.id.testResults), 0);
            builder.mActions.clear();
            builder.setContentTitle(getApplicationContext().getString(R.string.Notification_FinishedRunning))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setProgress(100,100,false);
            notificationManager.notify(1, builder.build());
        }
        else
            notificationManager.cancel(NOTIFICATION_ID);
        this.unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class TestBinder extends Binder {
        public RunTestService getService() {
            return RunTestService.this;
        }
    }

    static boolean shouldShowNotification(Context context) {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;

        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        // app is in foreground, but if screen is locked show notification anyway
        return km.inKeyguardRestrictedInputMode();
    }

    public class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null && action.equals("interrupt_test")){
                stopTest();
            }
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        public void stopTest(){
            task.interrupt();
        }
    }

}
