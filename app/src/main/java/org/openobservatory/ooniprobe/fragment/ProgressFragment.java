package org.openobservatory.ooniprobe.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.test.TestAsyncTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressFragment extends Fragment implements ServiceConnection {
    private RunTestService service;
    private ProgressFragment.TestRunBroadRequestReceiver receiver;
    boolean isBound = false;

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
        receiver = new ProgressFragment.TestRunBroadRequestReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        //Bind the RunTestService
        if (((Application)getActivity().getApplication()).isTestRunning()) {
            Intent intent = new Intent(getActivity(), RunTestService.class);
            getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
            progress_layout.setVisibility(View.VISIBLE);
        }
        else
            progress_layout.setVisibility(View.GONE);
    }

    private void updateUI(){
        progress.setIndeterminate(true);
        if (service != null && service.task != null){
            if (service.task.currentSuite != null)
                progress.setMax(service.task.currentSuite.getTestList(preferenceManager).length * 100);
            if (service.task.currentTest != null)
                name.setText(getString(service.task.currentTest.getLabelResId()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isBound) {
            getActivity().unbindService(this);
            isBound = false;
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onServiceConnected(ComponentName cname, IBinder binder) {
        //Bind the service to this activity
        RunTestService.TestBinder b = (RunTestService.TestBinder) binder;
        service = b.getService();
        isBound = true;
        if (((Application)getActivity().getApplication()).isTestRunning())
            updateUI();
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
                    progress.setIndeterminate(true);
                    progress.setMax(service.task.currentSuite.getTestList(preferenceManager).length * 100);
                    break;
                case TestAsyncTask.RUN:
                    name.setText(value);
                    break;
                case TestAsyncTask.PRG:
                    if (progress.isIndeterminate())
                        progress.setMax(service.task.currentSuite.getTestList(preferenceManager).length * 100);
                    int prgs = Integer.parseInt(value);
                    progress.setIndeterminate(false);
                    progress.setProgress(prgs);
                    break;
                case TestAsyncTask.URL:
                    progress.setIndeterminate(false);
                    break;
                case TestAsyncTask.INT:
                    running.setText(getString(R.string.Dashboard_Running_Stopping_Title));
                    break;
                case TestAsyncTask.END:
                    progress_layout.setVisibility(View.GONE);
                    break;
            }
        }
    }
}