package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.adapter.PastTestsListAdapter;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.utils.EmptyRecyclerView;

import java.util.ArrayList;

public class PastTestsFragment extends Fragment {
    private MainActivity mActivity;
    private EmptyRecyclerView mPastTestsListView;
    private PastTestsListAdapter mPastTestsListAdapter;

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
        mActivity.setTitle(mActivity.getString(R.string.past_tests));
        updateList();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_clean_tests, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_past_tests, container, false);
        mPastTestsListView = (EmptyRecyclerView) v.findViewById(R.id.pastTests);

        mPastTestsListAdapter = new PastTestsListAdapter(mActivity, new ArrayList<NetworkMeasurement>());
        mPastTestsListView.setAdapter(mPastTestsListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mPastTestsListView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mPastTestsListView.getContext(),
                layoutManager.getOrientation());
        mPastTestsListView.addItemDecoration(dividerItemDecoration);
        TestStorage.resetNewTests(mActivity);
        mActivity.updateActionBar();
        mPastTestsListView.setEmptyView(v.findViewById(R.id.empty_tests_view));

        AppCompatButton runTest = (AppCompatButton) v.findViewById(R.id.run_test_button);
        runTest.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        mActivity.selectItem(0);
                    }
                }
        );
        return v;
    }

    public void updateList(){
        mPastTestsListAdapter.setData(TestStorage.loadTestsReverse(mActivity));
    }
}
