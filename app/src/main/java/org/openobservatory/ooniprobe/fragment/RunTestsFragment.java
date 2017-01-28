package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.adapter.RunTestListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;

public class RunTestsFragment extends Fragment {
    private MainActivity mActivity;
    private RecyclerView mAvailableTestsListView;
    private RunTestListAdapter mAvailableTestsListAdapter;
    private static TestStorage ts;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (MainActivity) activity;
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
    public void onResume() {
        super.onResume();
        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mActivity.setTitle(mActivity.getString(R.string.run_tests));
        updateList();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run_tests, container, false);
        mAvailableTestsListView = (RecyclerView) v.findViewById(R.id.runTests);
        mAvailableTestsListAdapter = new RunTestListAdapter(mActivity, TestData.getInstance(mActivity).availableTests);
        mAvailableTestsListView.setAdapter(mAvailableTestsListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mAvailableTestsListView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mAvailableTestsListView.getContext(),
                layoutManager.getOrientation());
        mAvailableTestsListView.addItemDecoration(dividerItemDecoration);

        return v;
    }

    public void updateList(){
        mAvailableTestsListAdapter.setData(TestData.getInstance(mActivity).availableTests);
    }
}
