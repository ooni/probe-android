package org.openobservatory.ooniprobe.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.app.fragment.MessageDialogFragment;

public class RunningActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener, ServiceConnection {
    private static final String TEST = "test";
    private static final String BACKGROUND_TASK = "background";
    @BindView(R.id.running)
    TextView running;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.log)
    TextView log;
    @BindView(R.id.eta)
    TextView eta;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.close)
    ImageButton close;
    @BindView(R.id.animation)
    LottieAnimationView animation;
    private ArrayList<AbstractSuite> testSuites;
    private AbstractSuite testSuite;
    //private AbstractSuite testSuite;
    //TODO maybe not needed anymore
    private boolean background;
    private Integer runtime;
    private TestAsyncTask task;
    private RunTestService service;
    private MyBroadRequestReceiver receiver;

    public static Intent newIntent(AbstractActivity context, ArrayList<AbstractSuite> testSuites) {
        if (ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            new MessageDialogFragment.Builder()
                    .withTitle(context.getString(R.string.Modal_Error))
                    .withMessage(context.getString(R.string.Modal_Error_NoInternet))
                    .build().show(context.getSupportFragmentManager(), null);
            return null;
        } else {
            Bundle extra = new Bundle();
            extra.putSerializable(TEST, testSuites);
            return new Intent(context, RunningActivity.class).putExtra(TEST, extra);
        }
    }

    public static Intent newIntent(Context context, ArrayList<AbstractSuite> testSuites) {
        Bundle extra = new Bundle();
        extra.putSerializable(TEST, testSuites);
        return new Intent(context, RunningActivity.class).putExtra(TEST, extra);
    }

    //TODO remove
    public static Intent newBackgroundIntent(Context context, ArrayList<AbstractSuite> testSuites) {
        return newIntent(context, testSuites).putExtra("background", true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO remove
        //With this function we can move the activity to the background
        //https://stackoverflow.com/questions/10008879/intent-to-start-activity-but-dont-bring-to-front
        if (getIntent().getBooleanExtra(BACKGROUND_TASK, false))
            moveTaskToBack(true);

        setTheme(R.style.Theme_MaterialComponents_NoActionBar_App);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);
        Bundle extra = getIntent().getBundleExtra(TEST);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_InterruptTest_Title))
                        .withMessage(getString(R.string.Modal_InterruptTest_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .withNegativeButton(getString(R.string.Modal_Cancel))
                        .build().show(getSupportFragmentManager(), null);
            }
        });

        //if (extra != null)
        testSuites = (ArrayList<AbstractSuite>) extra.getSerializable(TEST);
        if (testSuites == null) {
            finish();
            return;
        }
        startService();
    }

    private void runTest() {
        /*if (testSuites.size() > 0) {
            testSuite = testSuites.get(0);
            testStart();
            setTestRunning(true);
            startService();
            //task = (TestAsyncTaskImpl) new TestAsyncTaskImpl(this, (Application) getApplication(), testSuite.getResult()).execute(testSuite.getTestList(getPreferenceManager()));
        }
         */
    }
    private void applyUIChanges(int index){
        testSuite = testSuites.get(index);
        applyUIChanges(testSuite);
    }

    private void applyUIChanges(AbstractSuite testSuite){
        runtime = testSuite.getRuntime(getPreferenceManager());
        getWindow().setBackgroundDrawableResource(testSuite.getColor());
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(testSuite.getColor());
        }
        animation.setImageAssetsFolder("anim/");
        animation.setAnimation(testSuite.getAnim());
        animation.setRepeatCount(Animation.INFINITE);
        animation.playAnimation();
        progress.setIndeterminate(true);
        eta.setText(R.string.Dashboard_Running_CalculatingETA);
        progress.setMax(testSuite.getTestList(getPreferenceManager()).length * 100);
    }

    //TODO https://stackoverflow.com/questions/55486812/using-a-broadcastreceiver-to-update-a-progressbar
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new MyBroadRequestReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Intent intent= new Intent(this, RunTestService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        background = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        background = true;
    }

    @Override
    protected void onDestroy() {
        //TODO use service running
        setTestRunning(false);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.Modal_Error_CantCloseScreen), Toast.LENGTH_SHORT).show();
    }

    public void startService() {
        //TODO use service running
        setTestRunning(true);
        Intent serviceIntent = new Intent(this, RunTestService.class);
        serviceIntent.putExtra("testSuites", testSuites);
        ContextCompat.startForegroundService(this, serviceIntent);
        //service.setOnProgressChangedListener(this);
    }

    /*
    @Override
    public void onProgressUpdate(int progress) {
        // Do update your progress...
    }
    */

    //TODO this should stop the test. Do the relative close functions in there
    public void stopService() {
        Intent serviceIntent = new Intent(this, RunTestService.class);
        stopService(serviceIntent);
        Toast.makeText(RunningActivity.this, "DISConnected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServiceConnected(ComponentName cname, IBinder binder) {
        RunTestService.TestBinder b = (RunTestService.TestBinder) binder;
        service = b.getService();
        applyUIChanges(service.task.currentSuite);
        if (service.task.currentTest != null)
            name.setText(getString(service.task.currentTest.getLabelResId()));
        Toast.makeText(RunningActivity.this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    //TODO if activity is opened when a test is running we won't get START (wrong background)
    //TODO Create getCurrentTest method onResume
    public class MyBroadRequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String value = intent.getStringExtra("value");
            switch (key) {
                case TestAsyncTask.START:
                    applyUIChanges(Integer.parseInt(value));
                    break;
                case TestAsyncTask.RUN:
                    name.setText(value);
                    break;
                case TestAsyncTask.PRG:
                    progress.setIndeterminate(false);
                    int prgs = Integer.parseInt(value);
                    progress.setProgress(prgs);
                    eta.setText(getString(R.string.Dashboard_Running_Seconds, String.valueOf(Math.round(runtime - ((double) prgs) / progress.getMax() * runtime))));
                    break;
                case TestAsyncTask.LOG:
                    log.setText(value);
                    break;
                case TestAsyncTask.ERR:
                    Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case TestAsyncTask.URL:
                    progress.setIndeterminate(false);
                    runtime = testSuite.getRuntime(getPreferenceManager());
                    break;
                case TestAsyncTask.END:
                    if (background) {
                        NotificationService.notifyTestEnded(context, testSuite);
                    } else
                        startActivity(MainActivity.newIntent(context, R.id.testResults));
                    stopService();
                    finish();
                    break;
            }
        }
    }
    //TODO remove impl and change with callbacks from service
    /*private static class TestAsyncTaskImpl<ACT extends AbstractActivity> extends TestAsyncTask {
        protected final WeakReference<ACT> ref;

        TestAsyncTaskImpl(ACT activity, Application app, Result result) {
            //super(app, result);
            this.ref = new WeakReference<>(activity);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //ACT act = ref.get();
            RunningActivity act = (RunningActivity)ref.get();
            if (act != null && !act.isFinishing())
                switch (values[0]) {
                    case RUN:
                        //TODO this should be fixed in the test to be able to see it even when the test is started
                        act.name.setText(values[1]);
                        break;
                    case PRG:
                        act.progress.setIndeterminate(false);
                        int prgs = Integer.parseInt(values[1]);
                        act.progress.setProgress(prgs);
                        act.eta.setText(act.getString(R.string.Dashboard_Running_Seconds, String.valueOf(Math.round(act.runtime - ((double) prgs) / act.progress.getMax() * act.runtime))));
                        break;
                    case LOG:
                        if (!act.task.isInterrupted())
                            act.log.setText(values[1]);
                        break;
                    case ERR:
                        Toast.makeText(act, values[1], Toast.LENGTH_SHORT).show();
                        act.finish();
                        break;
                    case URL:
                        act.progress.setIndeterminate(false);
                        act.runtime = act.testSuite.getRuntime(act.getPreferenceManager());
                        break;
                }
        }

        //TODO this execute next test. Will it work in the Service?
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RunningActivity act = (RunningActivity)ref.get();
            act.testSuites.remove(act.testSuite);
            if (act.testSuites.size() == 0)
                endTest(act);
            else
                act.runTest();
        }

        //TODO last test executed
        private void endTest(RunningActivity act){
            if (act != null && !act.isFinishing()) {
                if (act.background) {
                    NotificationService.notifyTestEnded(act, act.testSuite);
                } else
                    act.startActivity(MainActivity.newIntent(act, R.id.testResults));
                act.finish();
            }
        }
    }
*/
    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
            log.setText(getString(R.string.Dashboard_Running_Stopping_Notice));
            task.interrupt();
        }
    }
}
