package org.openobservatory.ooniprobe.common.service;

import android.annotation.SuppressLint;
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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
    private static final String TAG = RunTestService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(ACTION_INTERRUPT);
        receiver = new ActionReceiver();
        this.registerReceiver(receiver, filter);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new ProgressBroadcastReceiver(),
                new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity")
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<AbstractSuite> iTestSuites = (ArrayList<AbstractSuite>) intent.getSerializableExtra("testSuites");
        if (iTestSuites == null)
            return START_STICKY_COMPATIBILITY;
        ArrayList<AbstractSuite> testSuites = Lists.newArrayList(Iterables.filter(iTestSuites, testSuite -> !testSuite.isTestEmpty()));
        if (testSuites.size() == 0)
            return START_STICKY_COMPATIBILITY;
        boolean store_db = intent.getBooleanExtra("storeDB", true);
        Application app = ((Application) getApplication());
        NotificationUtility.setChannel(getApplicationContext(), CHANNEL_ID, app.getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
        Intent notificationIntent = new Intent(this, RunningActivity.class);
        notificationIntent.setPackage("org.openobservatory.ooniprobe");
        PendingIntent pendingIntent = pendingIntentGetActivity(
                this, 0, notificationIntent
        );
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getApplication().getString(R.string.Dashboard_Running_Running))
                .setContentText(getApplication().getString(R.string.Dashboard_Running_PreparingTest))
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setProgress(100, 0, false)
                .build();

        task = (TestAsyncTask) new TestAsyncTask(app, testSuites, store_db).execute();
        //This intent is used to manage the stop test button in the notification
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RunTestService.ACTION_INTERRUPT);
        PendingIntent pIntent = pendingIntentGetBroadcast(this, 1, broadcastIntent);
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shouldShowNotification(this)) {
            PendingIntent pendingIntent = pendingIntentGetActivity(
                    getApplicationContext(), 0,
                    MainActivity.newIntent(getApplicationContext(), R.id.testResults)
            );
            builder.mActions.clear();
            builder.setContentTitle(getApplicationContext().getString(R.string.Notification_FinishedRunning))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setProgress(100, 100, false);
            notificationManager.notify(1, builder.build());
        } else if (notificationManager != null)
            notificationManager.cancel(NOTIFICATION_ID);
        this.unregisterReceiver(receiver);
    }

    // pendingIntentGetActivity is a factory to correctly call PendingIntent.getActivity
    // with the correct mutability flags depending on the SDK version.
    private static PendingIntent pendingIntentGetActivity(Context ctx, int requestCode, Intent intent) {
        int flags = 0;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(ctx, requestCode, intent, flags);
    }

    // pendingIntentGetBroadcast is factory to correctly call PendingIntent.getBroadcast
    // with the correct mutability flags depending on the SDK version using.
    private static PendingIntent pendingIntentGetBroadcast(Context ctx, int requestCode, Intent intent) {
        int flags;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            flags = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.getBroadcast(ctx, requestCode, intent, flags);
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
            if (action != null && action.equals("interrupt_test")) {
                stopTest();
            }
            // INFO: No need to use ACTION_CLOSE_SYSTEM_DIALOGS in this situation.
            // That's because, if your app calls startActivity()
            // while a window is on top of the notification drawer,
            // the system closes the notification drawer automatically
            // and this happens on _any_ Android version.
            // https://developer.android.com/about/versions/12/behavior-changes-all?msclkid=3ad37f25cf7411ecb536010741f51e42#close-system-dialogs-exceptions
        }

        public void stopTest() {
            task.interrupt();
        }
    }

    /**
     * Interrupt running task by calling  TestAsyncTask#interrupt()
     *
     * @see TestAsyncTask#interrupt()
     */
    public synchronized void interrupt() {
        task.interrupt();
    }

    private class ProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String value = intent.getStringExtra("value");
            switch (key) {
                case TestAsyncTask.RUN:
                    Log.d(TAG, "TestAsyncTask.RUN");
                    try {
                        builder.setContentText(value);
                        builder.setProgress(task.currentSuite.getTestList(((Application) getApplication()).getPreferenceManager()).length * 100, 0, false);
                        notificationManager.notify(RunTestService.NOTIFICATION_ID, builder.build());
                    } catch (Exception e) {
                        ThirdPartyServices.logException(e);
                    }
                    break;
                case TestAsyncTask.PRG:
                    Log.d(TAG, "TestAsyncTask.PRG " + value);
                    try {
                        int prgs = Integer.parseInt(value);
                        builder.setProgress(task.currentSuite.getTestList(((Application) getApplication()).getPreferenceManager()).length * 100, prgs, false);
                        notificationManager.notify(RunTestService.NOTIFICATION_ID, builder.build());
                    } catch (Exception e) {
                        ThirdPartyServices.logException(e);
                    }
                    break;
                case TestAsyncTask.INT:
                    Log.d(TAG, "TestAsyncTask.INT");
                    try {
                        builder.setContentText(getString(R.string.Dashboard_Running_Stopping_Title))
                                .setProgress(0, 0, true);
                        notificationManager.notify(RunTestService.NOTIFICATION_ID, builder.build());
                    } catch (Exception e) {
                        ThirdPartyServices.logException(e);
                    }
                    break;
                case TestAsyncTask.END:
                    Log.d(TAG, "TestAsyncTask.END");
                    try {
                        stopSelf();
                    } catch (Exception e) {
                        ThirdPartyServices.logException(e);
                    }
                    break;
            }
        }
    }
}
