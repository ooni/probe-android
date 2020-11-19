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
import org.openobservatory.ooniprobe.common.CountlyManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
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
    private Integer runtime;
    private RunTestService service;
    private TestRunBroadRequestReceiver receiver;
    boolean isBound = false;

    public static Intent newIntent(AbstractActivity context, ArrayList<AbstractSuite> testSuites) {
        if (ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            new MessageDialogFragment.Builder()
                    .withTitle(context.getString(R.string.Modal_Error))
                    .withMessage(context.getString(R.string.Modal_Error_NoInternet))
                    .build().show(context.getSupportFragmentManager(), null);
            return null;
        } else {
            Intent serviceIntent = new Intent(context, RunTestService.class);
            serviceIntent.putExtra("testSuites", testSuites);
            ContextCompat.startForegroundService(context, serviceIntent);
            return new Intent(context, RunningActivity.class);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MaterialComponents_NoActionBar_App);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);
        CountlyManager.recordView("TestRunning");
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
    }

    private void applyUIChanges(){
        if (service == null || service.task == null ||
            service.task.currentSuite == null || service.task.currentTest == null) {
            return;
        }
        animation.setImageAssetsFolder("anim/");
        animation.setRepeatCount(Animation.INFINITE);
        animation.playAnimation();
        progress.setIndeterminate(true);
        eta.setText(R.string.Dashboard_Running_CalculatingETA);

        name.setText(getString(service.task.currentTest.getLabelResId()));
        runtime = service.task.currentSuite.getRuntime(getPreferenceManager());
        getWindow().setBackgroundDrawableResource(service.task.currentSuite.getColor());
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(service.task.currentSuite.getColor());
        }
        animation.setAnimation(service.task.currentSuite.getAnim());
        progress.setMax(service.task.currentSuite.getTestList(getPreferenceManager()).length * 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isTestRunning()) {
            testEnded(this);
            return;
        }
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new TestRunBroadRequestReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        //Bind the RunTestService
        Intent intent = new Intent(this, RunTestService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.Modal_Error_CantCloseScreen), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceConnected(ComponentName cname, IBinder binder) {
        RunTestService.TestBinder b = (RunTestService.TestBinder) binder;
        service = b.getService();
        isBound = true;
        applyUIChanges();
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
                    applyUIChanges();
                    break;
                case TestAsyncTask.RUN:
                    name.setText(value);
                    break;
                case TestAsyncTask.PRG:
                    int prgs = Integer.parseInt(value);
                    progress.setIndeterminate(false);
                    progress.setProgress(prgs);
                    if (runtime != null)
                        eta.setText(getString(R.string.Dashboard_Running_Seconds,
                                String.valueOf(Math.round(runtime - ((double) prgs) / progress.getMax() * runtime))));
                    break;
                case TestAsyncTask.LOG:
                    log.setText(value);
                    break;
                case TestAsyncTask.ERR:
                    Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                    break;
                case TestAsyncTask.URL:
                    progress.setIndeterminate(false);
                    runtime = service.task.currentSuite.getRuntime(getPreferenceManager());
                    break;
                case TestAsyncTask.INT:
                    running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
                    log.setText(getString(R.string.Dashboard_Running_Stopping_Notice));
                case TestAsyncTask.END:
                    testEnded(context);
                    break;
            }
        }
    }

    private void testEnded(Context context) {
        startActivity(MainActivity.newIntent(context, R.id.testResults));
        finish();
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (service != null)
                service.task.interrupt();
        }
    }
}
