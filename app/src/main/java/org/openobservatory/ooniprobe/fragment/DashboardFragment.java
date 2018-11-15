package org.openobservatory.ooniprobe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.item.TestsuiteItem;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.StartEndSpacesItemDecoration;

public class DashboardFragment extends Fragment implements View.OnClickListener {
	@BindView(R.id.recycler) RecyclerView recycler;
	@BindView(R.id.toolbar) Toolbar toolbar;
	private ArrayList<TestsuiteItem> items;
	private HeterogeneousRecyclerAdapter<TestsuiteItem> adapter;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (bar != null) {
			bar.setDisplayShowCustomEnabled(true);
			bar.setCustomView(R.layout.logo);
		}
		items = new ArrayList<>();
		PreferenceManager pm = ((Application) getActivity().getApplication()).getPreferenceManager();
		items.add(new TestsuiteItem(new WebsitesSuite(), pm, this));
		items.add(new TestsuiteItem(new InstantMessagingSuite(), pm, this));
		items.add(new TestsuiteItem(new PerformanceSuite(), pm, this));
		items.add(new TestsuiteItem(new MiddleBoxesSuite(), pm, this));
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		recycler.addItemDecoration(new StartEndSpacesItemDecoration(getActivity(), RecyclerView.VERTICAL, 8, 8));
		return v;
	}

	@Override public void onClick(View v) {
		AbstractSuite testSuite = (AbstractSuite) v.getTag();
		switch (v.getId()) {
			case R.id.run:
				Intent intent = RunningActivity.newIntent((AbstractActivity) getActivity(), testSuite);
				if (intent != null)
					ActivityCompat.startActivity(getActivity(), intent, ActivityOptionsCompat.makeClipRevealAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle());
				break;
			default:
				ActivityCompat.startActivity(getActivity(), OverviewActivity.newIntent(getActivity(), testSuite), ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
						Pair.create(v, getString(R.string.transitionNameCard)),
						Pair.create(v.findViewById(R.id.icon), getString(R.string.transitionNameIcon)),
						//	Pair.create(v.findViewById(R.id.title), getString(R.string.transitionNameTitle)),
						Pair.create(v.findViewById(R.id.runtime), getString(R.string.transitionNameRuntime)),
						Pair.create(v.findViewById(R.id.run), getString(R.string.transitionNameRun))
				).toBundle());
				break;
		}
	}
}
