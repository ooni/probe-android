package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.ResultActivity;
import org.openobservatory.ooniprobe.utils.LogUtils;

public class TestLogFragment extends Fragment {
    private ResultActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (ResultActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onViewSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_log, container, false);
        String json_file = getActivity().getIntent().getExtras().getString("log_file");
        TextView tv = (TextView) v.findViewById(R.id.logView);
        String log_text = LogUtils.readLogFile(mActivity, json_file);
        tv.setText(log_text);
        return v;
    }

}