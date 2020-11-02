package org.openobservatory.ooniprobe.common.service;

import android.app.Notification;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
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
        ActionReceiver receiver = new ActionReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<AbstractSuite> testSuites = (ArrayList<AbstractSuite>) intent.getSerializableExtra("testSuites");
        Application app = ((Application)getApplication());
        NotificationService.setChannel(getApplicationContext(), CHANNEL_ID, app.getString(R.string.Settings_AutomatedTesting_Label));
        Intent notificationIntent = new Intent(this, RunningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getApplication().getString(R.string.Dashboard_Running_Running))
                .setContentText(getApplication().getString(R.string.Dashboard_Running_PreparingTest))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setProgress(100, 0, false)
                //.addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .build();


        task = (TestAsyncTask) new TestAsyncTask(app, testSuites, this).execute();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RunTestService.ACTION_INTERRUPT);
        PendingIntent pIntent = PendingIntent.getBroadcast(this,1, broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ooni_logo, "STOP TEST", pIntent);

        startForeground(NOTIFICATION_ID, builder.build());

        //stopSelf();

        return START_NOT_STICKY;
    }

    //TODO-SERVICE test this if it's too intrusive when app foreground
    @Override
    public void onDestroy() {
        super.onDestroy();
        builder.setContentText(getApplicationContext().getString(R.string.Notification_FinishedRunning))
                .setProgress(0,0,false);
        notificationManager.notify(1, builder.build());
        if (receiver != null)
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

    public class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("stopTest onReceive is being called1.1 " + action);
            if(action != null && action.equals("interrupt_test")){
                stopTest(context);
            }
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        public void stopTest(Context context){
            //TODO-SERVICE call interrupt test
            System.out.println("stopTest is being called1");
            task.interrupt();
        }
    }

}
