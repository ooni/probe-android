package org.openobservatory.ooniprobe.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.test.TestAsyncTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment implements ServiceConnection {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RunTestService service;
    private ProgressFragment.TestRunBroadRequestReceiver receiver;
    boolean isBound = false;

    @Inject
    PreferenceManager preferenceManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_progress, container, false);
        ButterKnife.bind(this, v);
        ((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
        // Inflate the layout for this fragment
        if (((Application)getActivity().getApplication()).isTestRunning())
            progress_layout.setVisibility(View.VISIBLE);
        else
            progress_layout.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("ProgressFragment onResume");

        /*if (!isTestRunning()) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel(RunTestService.NOTIFICATION_ID);
            testEnded(this);
            return;
        }*/
        //TODO change
        IntentFilter filter = new IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity");
        receiver = new ProgressFragment.TestRunBroadRequestReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        //Bind the RunTestService
        if (((Application)getActivity().getApplication()).isTestRunning()) {
            System.out.println("ProgressFragment onResume isTestRunning");
            Intent intent = new Intent(getActivity(), RunTestService.class);
            getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
            progress_layout.setVisibility(View.VISIBLE);
        }
        else
            progress_layout.setVisibility(View.GONE);
    }

    private void updateUI(){
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
        System.out.println("ProgressFragment onPause");
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
        System.out.println("ProgressFragment onServiceConnected");
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
            System.out.println("ProgressFragment TestRunBroadRequestReceiver ");
            //TODO hide on test .END
            if (((Application)getActivity().getApplication()).isTestRunning())
                progress_layout.setVisibility(View.VISIBLE);
            else
                progress_layout.setVisibility(View.GONE);

            String key = intent.getStringExtra("key");
            System.out.println("ProgressFragment TestRunBroadRequestReceiver "+ key);

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
            }
        }
    }
}