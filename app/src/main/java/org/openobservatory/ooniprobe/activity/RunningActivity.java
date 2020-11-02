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
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.app.fragment.MessageDialogFragment;

public class RunningActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener, ServiceConnection {
    private static final String TEST = "test";
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
    private boolean background;
    private Integer runtime;
    private RunTestService service;
    private TestRunBroadRequestReceiver receiver;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        testSuites = (ArrayList<AbstractSuite>) extra.getSerializable(TEST);
        if (testSuites == null) {
            finish();
            return;
        }
        startService();
    }

    private void applyUIChanges(int index){
        testSuite = testSuites.get(index);
        applyUIChanges(testSuite);
    }

    private void applyUIChanges(AbstractSuite testSuite){
        if (testSuite == null)
            return;
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

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new TestRunBroadRequestReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        //Bind the RunTestService
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
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.Modal_Error_CantCloseScreen), Toast.LENGTH_SHORT).show();
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, RunTestService.class);
        serviceIntent.putExtra("testSuites", testSuites);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    //TODO-SERVICE this should stop the test. Do the relative close functions in there
    public void stopService() {
        Intent serviceIntent = new Intent(this, RunTestService.class);
        stopService(serviceIntent);
        finish();
    }

    @Override
    public void onServiceConnected(ComponentName cname, IBinder binder) {
        RunTestService.TestBinder b = (RunTestService.TestBinder) binder;
        service = b.getService();
        applyUIChanges(service.task.currentSuite);
        if (service.task.currentTest != null)
            name.setText(getString(service.task.currentTest.getLabelResId()));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    public class TestRunBroadRequestReceiver extends BroadcastReceiver {
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
                    //TODO-SERVICE shouldn't be thiss callback to stop
                    Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                    stopService();
                    break;
                case TestAsyncTask.URL:
                    progress.setIndeterminate(false);
                    runtime = testSuite.getRuntime(getPreferenceManager());
                    break;
                case TestAsyncTask.INT:
                    running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
                    log.setText(getString(R.string.Dashboard_Running_Stopping_Notice));
                case TestAsyncTask.END:
                    //TODO-SERVICE this can be removed if we use the onDestroy of the Service
                    if (background) {
                        NotificationService.notifyTestEnded(context, testSuite);
                    } else
                        startActivity(MainActivity.newIntent(context, R.id.testResults));
                    //TODO-SERVICE I don't think the activity should be the one in charge to stop the service
                    stopService();
                    break;
            }
        }
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (service != null)
                service.task.interrupt();
        }
    }
}
