package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import org.openobservatory.ooniprobe.activity.ResultActivity;
import org.openobservatory.ooniprobe.adapter.TestResultListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.TestResult;
import org.openobservatory.ooniprobe.utils.LogUtils;

import java.util.ArrayList;

public class ResultListFragment extends Fragment {
    private RecyclerView testResultList;
    private TestResultListAdapter mResultTestsListAdapter;
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
        View v =inflater.inflate(R.layout.fragment_result_list, container, false);
        String json_file = getActivity().getIntent().getExtras().getString("json_file");
        final String[] parts = LogUtils.getLogParts(getActivity(), json_file);
        ArrayList<TestResult> listItems = new ArrayList<>();
        try {
            for(String str:parts) {
                JSONObject jsonObj = new JSONObject(str);
                int anomaly = 0;
                JSONObject blocking = jsonObj.getJSONObject("test_keys");
                Object object = blocking.get("blocking");
                if(object instanceof String)
                    anomaly = 2;
                else if(object instanceof Boolean)
                    anomaly = 0;
                else
                    anomaly = 1;
                TestResult result = new TestResult(jsonObj.getString("input"), anomaly);
                listItems.add(result);
            }
        } catch (JSONException e) {
        }
        testResultList = (RecyclerView) v.findViewById(R.id.resultList);
        mResultTestsListAdapter = new TestResultListAdapter(getActivity(), listItems);
        testResultList.setAdapter(mResultTestsListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        testResultList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(testResultList.getContext(),
                layoutManager.getOrientation());
        testResultList.addItemDecoration(dividerItemDecoration);
        mResultTestsListAdapter.setData(listItems);

        return v;
    }
}
