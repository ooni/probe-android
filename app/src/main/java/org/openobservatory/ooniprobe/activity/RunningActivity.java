package org.openobservatory.ooniprobe.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.common.collect.Lists;
import com.google.common.math.Stats;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.reciver.TestRunBroadRequestReceiver;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.app.fragment.MessageDialogFragment;

public class RunningActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
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
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.animation)
    LottieAnimationView animation;
    @BindView(R.id.proxy_icon)
    RelativeLayout proxy_icon;
    private TestRunBroadRequestReceiver receiver;

    @Inject
    PreferenceManager preferenceManager;

    public static void runAsForegroundService(AbstractActivity context,
                                              ArrayList<AbstractSuite> testSuites,
                                              OnTestServiceStartedListener onTestServiceStartedListener) {
        if (ReachabilityManager.getNetworkType(context).equals(ReachabilityManager.NO_INTERNET)) {
            new MessageDialogFragment.Builder()
                    .withTitle(context.getString(R.string.Modal_Error))
                    .withMessage(context.getString(R.string.Modal_Error_NoInternet))
                    .build().show(context.getSupportFragmentManager(), null);
        } else if (context.isTestRunning()) {
            new MessageDialogFragment.Builder()
                    .withTitle(context.getString(R.string.Modal_Error))
                    .withMessage(context.getString(R.string.Modal_Error_TestAlreadyRunning))
                    .build().show(context.getSupportFragmentManager(), null);
        } else if (ReachabilityManager.isVPNinUse(context)) {
            new AlertDialog.Builder(context, R.style.MaterialAlertDialogCustom)
                    .setTitle(context.getString(R.string.Modal_DisableVPN_Title))
                    .setMessage(context.getString(R.string.Modal_DisableVPN_Message))
                    .setNegativeButton(R.string.Modal_RunAnyway, (dialogInterface, i) -> {
                        startRunTestService(context, testSuites,onTestServiceStartedListener);
                    })
                    .setPositiveButton(R.string.Modal_DisableVPN, (dialogInterface, i) -> {
                        ((Application) context.getApplication()).openVPNSettings();
                    })
                    .show();
        } else {
            startRunTestService(context, testSuites,onTestServiceStartedListener);
        }
    }

    private static void startRunTestService(AbstractActivity context,
                                            ArrayList<AbstractSuite> testSuites,
                                            OnTestServiceStartedListener onTestServiceStartedListener) {
        Intent serviceIntent = new Intent(context, RunTestService.class);
        serviceIntent.putExtra("testSuites", testSuites);
        ContextCompat.startForegroundService(context, serviceIntent);
        onTestServiceStartedListener.onTestServiceStarted();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setTheme(R.style.Theme_MaterialComponents_NoActionBar_App);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);

        if (getPreferenceManager().getProxyURL().isEmpty())
            proxy_icon.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
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

    private void applyUIChanges(RunTestService service) {
        if (service == null || service.task == null ||
            service.task.currentSuite == null || service.task.currentTest == null) {
            return;
        }
        animation.setImageAssetsFolder("anim/");
        animation.setRepeatCount(Animation.INFINITE);
        animation.playAnimation();
        progress.setIndeterminate(true);
        eta.setText(R.string.Dashboard_Running_CalculatingETA);
        if (service.task.currentSuite.getName().equals(ExperimentalSuite.NAME))
            name.setText(service.task.currentTest.getName());
        else
            name.setText(getString(service.task.currentTest.getLabelResId()));
        getWindow().setBackgroundDrawableResource(service.task.currentSuite.getColor());
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(service.task.currentSuite.getColor());
        }
        animation.setAnimation(service.task.currentSuite.getAnim());
        progress.setMax((int)Stats.of(Lists.transform(
                service.task.testSuites,
                input -> input.getTestList(preferenceManager).length * 100
        )).sum());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isTestRunning()) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel(RunTestService.NOTIFICATION_ID);
            testEnded(this);
            return;
        }
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new TestRunBroadRequestReceiver(preferenceManager, new TestRunnerEventListener());
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        //Bind the RunTestService
        Intent intent = new Intent(this, RunTestService.class);
//        bindService(intent, this, Context.BIND_AUTO_CREATE);
        bindService(intent, receiver, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver.isBound()) {
            unbindService(receiver);
            receiver.setBound(false);
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

    private void testEnded(Context context) {
        startActivity(MainActivity.newIntent(context, R.id.testResults));
        finish();
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (receiver.service != null)
                receiver.service.task.interrupt();
        }
    }

    public interface OnTestServiceStartedListener {
        void onTestServiceStarted();
    }

    private class TestRunnerEventListener implements TestRunBroadRequestReceiver.EventListener {
        @Override
        public void onStart(RunTestService service) {
            applyUIChanges(service);
        }

        @Override
        public void onRun(String value) {
            name.setText(value);
        }

        @Override
        public void onProgress(int state, double timeLeft) {
            progress.setIndeterminate(false);
            progress.setProgress(state);
            eta.setText(getString(R.string.Dashboard_Running_Seconds,
                    String.valueOf(Math.round(timeLeft))));
        }

        @Override
        public void onLog(String value) {
            log.setText(value);
        }

        @Override
        public void onError(String value) {
            Toast.makeText(RunningActivity.this, value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUrl() {
            progress.setIndeterminate(false);
        }

        @Override
        public void onInt() {
            running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
            log.setText(getString(R.string.Dashboard_Running_Stopping_Notice));
        }

        @Override
        public void onEnd(Context context) {
            testEnded(context);
        }
    }
}
