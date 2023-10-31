package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.TestProgressRepository;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.databinding.FragmentProgressBinding;
import org.openobservatory.ooniprobe.receiver.TestRunBroadRequestReceiver;

import javax.inject.Inject;

/**
 * Monitors and displays progress of {@link RunTestService}.
 */
public class ProgressFragment extends Fragment {
    private TestRunBroadRequestReceiver receiver;

    private FragmentProgressBinding biding;

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    TestProgressRepository testProgressRepository;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        biding = FragmentProgressBinding.inflate(inflater, container, false);
        ((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
        biding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RunningActivity.class);
            ActivityCompat.startActivity(getActivity(), intent, null);
        });
        testProgressRepository.getProgress().observe(getViewLifecycleOwner(),progressValue -> {
            if (progressValue!=null) {
                biding.progress.setProgress(progressValue);
            }
        });
        return biding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new TestRunBroadRequestReceiver(preferenceManager, new TestRunnerEventListener(),testProgressRepository);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        //Bind the RunTestService
        this.bindTestService();
    }

    public void bindTestService() {
        Activity activity = getActivity();
        if (activity!=null && ((Application)activity.getApplication()).isTestRunning()) {
            Intent intent = new Intent(getActivity(), RunTestService.class);
            getActivity().bindService(intent, receiver, Context.BIND_AUTO_CREATE);
            biding.progressLayout.setVisibility(View.VISIBLE);
        }
        else
            biding.progressLayout.setVisibility(View.GONE);
    }

    private void updateUI(RunTestService service){
        Activity activity = getActivity();
        if (activity!=null && ((Application)activity.getApplication()).isTestRunning()){

            Integer progressLevel = testProgressRepository.getProgress().getValue();
            if (progressLevel != null) {
                biding.progress.setProgress(progressLevel);
            } else {
                biding.progress.setIndeterminate(true);
            }
            if (service != null && service.task != null){
                if (service.task.currentSuite != null)
                    biding.progress.setMax(service.task.getMax(preferenceManager));
                if (service.task.currentTest != null)
                    biding.name.setText(getString(service.task.currentTest.getLabelResId()));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver.isBound()) {
            getActivity().unbindService(receiver);
            receiver.setBound(false);
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    private class TestRunnerEventListener implements TestRunBroadRequestReceiver.EventListener {
        @Override
        public void onStart(RunTestService service) {
            updateUI(service);
        }

        @Override
        public void onRun(String value) {
            biding.name.setText(value);
        }

        @Override
        public void onProgress(int state, double eta) {
            if (biding.progress.isIndeterminate())
                updateUI(receiver.service);
            biding.progress.setIndeterminate(false);
            biding.progress.setProgress(state);
        }

        @Override
        public void onLog(String value) {
            /* nothing */
        }

        @Override
        public void onError(String value) {
            /* nothing */
        }

        @Override
        public void onUrl() {
            biding.progress.setIndeterminate(false);
        }

        @Override
        public void onInterrupt() {
            biding.running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
        }

        @Override
        public void onEnd(Context context) {
            biding.progressLayout.setVisibility(View.GONE);
        }
    }
}