package org.openobservatory.ooniprobe.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import org.openobservatory.ooniprobe.adapter.TestResultListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.utils.LogUtils;

import java.util.ArrayList;

public class ResultListFragment extends Fragment {
    private RecyclerView testResultList;
    private TestResultListAdapter mResultTestsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_result_list, container, false);

        String json_file = getActivity().getIntent().getExtras().getString("json_file");
        final String[] parts = LogUtils.getLogParts(getActivity(), json_file);
        ArrayList<JSONObject> listItems = new ArrayList<>();
        try {
            for(String str:parts) {
                JSONObject jsonObj = new JSONObject(str);
                listItems.add(jsonObj);
            }
        } catch (JSONException e) {
        }
        testResultList = (RecyclerView) v.findViewById(R.id.resultList);
        mResultTestsListAdapter = new TestResultListAdapter(getActivity(), listItems);
        testResultList.setAdapter(mResultTestsListAdapter);
        testResultList.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mResultTestsListAdapter.setData(listItems);

        return v;
    }
}
