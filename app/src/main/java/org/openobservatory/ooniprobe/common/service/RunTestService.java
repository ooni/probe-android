package org.openobservatory.ooniprobe.common.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class RunTestService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
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
        //TODO use test name
        ArrayList<AbstractSuite> testSuites = (ArrayList<AbstractSuite>) intent.getSerializableExtra("testSuites");
        //AbstractSuite testSuite = (AbstractSuite)intent.getSerializableExtra("testSuite");

        //TODO move function to other class
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, RunningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //TODO move notification to other class
        //TODO add cancel action https://stackoverflow.com/questions/46750788/binding-foreground-services-with-activity
        //https://developer.android.com/training/notify-user/build-notification.html#progressbar
        //TODO add progress bar
        //TODO check if implementation is correct https://stackoverflow.com/questions/18094209/android-displaying-progress-in-a-notification

        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getApplication().getString(R.string.Dashboard_Running_Running))
                .setContentText(getApplication().getString(R.string.Dashboard_Running_PreparingTest))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setProgress(100, 0, false)
                //.addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .build();


        //notificationManager.notify(notificationId, builder.build());

        startForeground(NOTIFICATION_ID, builder.build());

        Application app = ((Application)getApplication());
        PreferenceManager pm = app.getPreferenceManager();
        task = (TestAsyncTask) new TestAsyncTask(app, testSuites, this).execute();
        //task = (TestAsyncTaskImpl) new TestAsyncTaskImpl(this, app, testSuites).execute();

        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        builder.setContentText("Download complete")
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
/*
    private static class TestAsyncTaskImpl<SRV extends RunTestService> extends TestAsyncTask {
        protected final WeakReference<SRV> ref;

        TestAsyncTaskImpl(SRV service, Application app, ArrayList<AbstractSuite> testSuites) {
            super(app, testSuites);
            this.ref = new WeakReference<>(service);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            RunTestService srv = (RunTestService)ref.get();
            srv.builder.setContentText("Download complete")
                    .setProgress(100,50,false);
            srv.notificationManager.notify(NOTIFICATION_ID, srv.builder.build());
        }

        //TODO this execute next test. Will it work in the Service?
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    */
}
