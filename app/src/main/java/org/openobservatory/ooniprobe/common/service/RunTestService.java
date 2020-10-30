package org.openobservatory.ooniprobe.common.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    private final IBinder mBinder = new TestBinder();
    public TestAsyncTask task;
    public NotificationCompat.Builder builder;
    public NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
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

        Intent intentAction = new Intent(getApplicationContext(), ActionReceiver.class);
        intentAction.putExtra("action","stop");
        PendingIntent pIntent = PendingIntent.getBroadcast(this,1, intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ooni_logo, "STOP TEST", pIntent);


        startForeground(NOTIFICATION_ID, builder.build());

        task = (TestAsyncTask) new TestAsyncTask(app, testSuites, this).execute();

        //stopSelf();

        return START_NOT_STICKY;
    }

    //TODO test this if it's too intrusive when app foreground
    @Override
    public void onDestroy() {
        super.onDestroy();
        builder.setContentText(getApplicationContext().getString(R.string.Notification_FinishedRunning))
                .setProgress(0,0,false);
        notificationManager.notify(1, builder.build());
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

    public static class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getStringExtra("action");
            if(action.equals("stop")){
                stopTest();
            }
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        public void stopTest(){
            //TODO call interrupt test
            //task.interrupt();
            System.out.println("stopTest is being called");
        }
    }

}
