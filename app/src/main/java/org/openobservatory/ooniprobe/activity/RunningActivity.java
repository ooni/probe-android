package org.openobservatory.ooniprobe.activity;

import static java.util.Locale.ENGLISH;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.TestProgressRepository;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.databinding.ActivityRunningBinding;
import org.openobservatory.ooniprobe.receiver.TestRunBroadRequestReceiver;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.app.fragment.MessageDialogFragment;

/**
 * Serves to display progress of {@code RunTestService} running in the background on a screen.
 *
 * Also contains {@link #runAsForegroundService(AbstractActivity, ArrayList<AbstractSuite>,OnTestServiceStartedListener) runAsForegroundService}
 *   used to start {@code RunTestService} in the background.
 */
public class RunningActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {

    ActivityRunningBinding binding;

    private TestRunBroadRequestReceiver receiver;

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    TestProgressRepository testProgressRepository;

    /**
     * Starts {@code RunTestService} in the background.
     *
     * @param context
     *  AbstractActivity of caller used in checking if test is already running,show dialog prompts
     *  as well as argument for ContextCompat#startForegroundService(Context,Intent).
     * @param testSuites
     *    List of test suites to run by {@code RunTestService}
     * @param onTestServiceStartedListener
     *    Listener for successful start of {@code RunTestService}
     */
    public static void runAsForegroundService(AbstractActivity context,
                                              ArrayList<AbstractSuite> testSuites,
                                              OnTestServiceStartedListener onTestServiceStartedListener,
                                              PreferenceManager iPreferenceManager) {

        if (iPreferenceManager.shouldShowTestProgressConsent()){
            context.startActivity(PromptActivity.newIntent(context, PromptActivity.Prompt.TEST_PROGRESS_CONSENT));
        }

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
        } else if (ReachabilityManager.isVPNinUse(context) && iPreferenceManager.isWarnVPNInUse()) {
            new AlertDialog.Builder(context, R.style.MaterialAlertDialogCustom)
                    .setTitle(context.getString(R.string.Modal_DisableVPN_Title))
                    .setMessage(context.getString(R.string.Modal_DisableVPN_Message))
                    .setNeutralButton(R.string.Modal_RunAnyway, (dialogInterface, i) -> {
                        startRunTestService(context, testSuites,onTestServiceStartedListener);
                    })
                    .setNegativeButton(R.string.Modal_AlwaysRun,(dialog, which) -> {
                        iPreferenceManager.setWarnVPNInUse(false);
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
        ServiceUtil.startRunTestServiceManual(context, testSuites, true);
        onTestServiceStartedListener.onTestServiceStarted();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setTheme(R.style.Theme_MaterialComponents_NoActionBar_App);
        binding = ActivityRunningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        testProgressRepository.getProgress().observe(this, progressValue -> {
            if (progressValue!=null) {
                binding.progress.setProgress(progressValue);
            }
        });
        testProgressRepository.getEta().observe(this,etaValue -> {
            if (etaValue!=null) {
                binding.eta.setText(readableTimeRemaining(etaValue));
            }
        });

        if (preferenceManager.getProxyURL().isEmpty())
            binding.proxyIcon.setVisibility(View.GONE);
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.stop.setOnClickListener(new View.OnClickListener() {
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
        binding.animation.setImageAssetsFolder("anim/");
        binding.animation.setRepeatCount(Animation.INFINITE);
        binding.animation.playAnimation();
        Integer progressLevel = testProgressRepository.getProgress().getValue();
        if (progressLevel != null) {
            binding.progress.setProgress(progressLevel);
        } else {
            binding.progress.setIndeterminate(true);
        }

        Double etaValue = testProgressRepository.getEta().getValue();
        if (etaValue!=null){
            binding.eta.setText(readableTimeRemaining(etaValue));
        }else {
            binding.eta.setText(R.string.Dashboard_Running_CalculatingETA);
        }

        if (Objects.equals(service.task.currentSuite.getName(),OONITests.EXPERIMENTAL.getLabel())) {
            binding.name.setText(service.task.currentTest.getName());
        } else {
            binding.name.setText(getString(service.task.currentTest.getLabelResId()));
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(service.task.currentSuite.getColor()));
        getWindow().setStatusBarColor(service.task.currentSuite.getColor());
        if (service.task.currentSuite.getAnim() == null){
            binding.animation.setImageResource(service.task.currentSuite.getIconGradient());
            binding.animation.setColorFilter(getResources().getColor(R.color.color_gray2));
            binding.animation.setPadding(0,100,0,100);
        } else {
            binding.animation.setAnimation(service.task.currentSuite.getAnim());
        }

        binding.progress.setMax(service.task.getMax(preferenceManager));
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
        receiver = new TestRunBroadRequestReceiver(preferenceManager, new TestRunnerEventListener(), testProgressRepository);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        //Bind the RunTestService
        Intent intent = new Intent(this, RunTestService.class);
        bindService(intent, receiver, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver!=null && receiver.isBound()) {
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

    private void testEnded(Context context) {
        startActivity(MainActivity.newIntent(context, R.id.testResults,"Probe Run complete"));
        finish();
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (receiver.service != null)
                receiver.service.interrupt();
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
            binding.name.setText(value);
        }

        @Override
        public void onProgress(int state, double timeLeft) {
            binding.progress.setIndeterminate(false);
            binding.progress.setProgress(state);

            binding.eta.setText(readableTimeRemaining(timeLeft));
        }

        @Override
        public void onLog(String value) {
            binding.log.setText(value);
        }

        @Override
        public void onError(String value) {
            Toast.makeText(RunningActivity.this, value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUrl() {
            binding.progress.setIndeterminate(false);
        }

        @Override
        public void onInterrupt() {
            binding.running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
            binding.log.setText(getString(R.string.Dashboard_Running_Stopping_Notice));
        }

        @Override
        public void onEnd(Context context) {
            testEnded(context);
        }
    }

    @NonNull
    public static String readableTimeRemaining(double timeLeft) {
        long letaValue = Math.round(timeLeft);
        return String.format(ENGLISH," %dm %02ds", letaValue/60, letaValue%60);
    }
}
