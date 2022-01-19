package org.openobservatory.ooniprobe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.item.TestsuiteItem;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;

public class DashboardFragment extends Fragment implements View.OnClickListener {
	@BindView(R.id.recycler) RecyclerView recycler;
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.last_tested) TextView lastTested;
    @BindView(R.id.run_all) TextView runAll;
	@BindView(R.id.vpn) TextView vpn;

	private ArrayList<TestsuiteItem> items;
	private ArrayList<AbstractSuite> testSuites;
	private HeterogeneousRecyclerAdapter<TestsuiteItem> adapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
		items = new ArrayList<>();
		testSuites = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		runAll.setOnClickListener(v1 -> runAll());
		return v;
	}

	@Override public void onResume() {
		super.onResume();
		items.clear();
		testSuites.clear();
		testSuites.addAll(TestAsyncTask.SUITES);
		for (AbstractSuite testSuite : testSuites)
			items.add(new TestsuiteItem(testSuite, this));
		setLastTest();
		adapter.notifyTypesChanged();
		if (ReachabilityManager.isVPNinUse(this.getContext()))
			vpn.setVisibility(View.VISIBLE);
		else
			vpn.setVisibility(View.GONE);
	}

	private void setLastTest() {
		Result lastResult = Result.getLastResult();
		if (lastResult == null)
			lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
					+ " " +
					getString(R.string.Dashboard_Overview_LastRun_Never));
		else
			lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
					+ " " +
					DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
	}

	public void runAll(){
		RunningActivity.runAsForegroundService((AbstractActivity) getActivity(), testSuites);
	}

	@Override public void onClick(View v) {
		AbstractSuite testSuite = (AbstractSuite) v.getTag();
		switch (v.getId()) {
			case R.id.run:
				Intent intent = RunningActivity.newIntent((AbstractActivity) getActivity(), testSuite.asArray());
				if (intent != null)
					ActivityCompat.startActivity(getActivity(), intent, null);
				break;
			default:
				ActivityCompat.startActivity(getActivity(), OverviewActivity.newIntent(getActivity(), testSuite), null);
				break;
		}
	}
}
