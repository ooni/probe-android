package org.openobservatory.ooniprobe.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.math.Stats;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.receiver.TestRunBroadRequestReceiver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Monitors and displays progress of {@link RunTestService}.
 */
public class ProgressFragment extends Fragment {
    private TestRunBroadRequestReceiver receiver;

    @Inject
    PreferenceManager preferenceManager;
    @BindView(R.id.progress_layout)
    FrameLayout progress_layout;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.running)
    TextView running;
    @BindView(R.id.name)
    TextView name;

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
        View v = inflater.inflate(R.layout.fragment_progress, container, false);
        ButterKnife.bind(this, v);
        ((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(getContext(), RunningActivity.class);
                    ActivityCompat.startActivity(getActivity(), intent, null);
                }
                return true;
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new TestRunBroadRequestReceiver(preferenceManager, new TestRunnerEventListener());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        //Bind the RunTestService
        this.bindTestService();
    }

    public void bindTestService() {
        if (((Application)getActivity().getApplication()).isTestRunning()) {
            Intent intent = new Intent(getActivity(), RunTestService.class);
            getActivity().bindService(intent, receiver, Context.BIND_AUTO_CREATE);
            progress_layout.setVisibility(View.VISIBLE);
        }
        else
            progress_layout.setVisibility(View.GONE);
    }

    private void updateUI(RunTestService service){
        if (((Application)getActivity().getApplication()).isTestRunning()){

            progress.setIndeterminate(true);
            if (service != null && service.task != null){
                if (service.task.currentSuite != null)
                progress.setMax(service.task.getMax(preferenceManager));
                if (service.task.currentTest != null)
                    name.setText(getString(service.task.currentTest.getLabelResId()));
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
            name.setText(value);
        }

        @Override
        public void onProgress(int state, double eta) {
            if (progress.isIndeterminate())
                updateUI(receiver.service);
            progress.setIndeterminate(false);
            progress.setProgress(state);
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
            progress.setIndeterminate(false);
        }

        @Override
        public void onInterrupt() {
            running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
        }

        @Override
        public void onEnd(Context context) {
            progress_layout.setVisibility(View.GONE);
        }
    }
}