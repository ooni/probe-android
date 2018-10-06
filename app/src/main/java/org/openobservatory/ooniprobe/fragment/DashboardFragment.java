package org.openobservatory.ooniprobe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.FirstLastSpacesItemDecoration;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;

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
		setHasOptionsMenu(true);
		items = new ArrayList<>();
		items.add(new TestsuiteItem(new WebsitesSuite(), this));
		items.add(new TestsuiteItem(new InstantMessagingSuite(), this));
		items.add(new TestsuiteItem(new PerformanceSuite(), this));
		items.add(new TestsuiteItem(new MiddleBoxesSuite(), this));
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		recycler.addItemDecoration(new FirstLastSpacesItemDecoration(getActivity(), RecyclerView.VERTICAL, 8));
		return v;
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_global));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override public void onClick(View v) {
		AbstractSuite testSuite = (AbstractSuite) v.getTag();
		switch (v.getId()) {
			case R.id.configure:
				startActivity(PreferenceActivity.newIntent(getActivity(), testSuite.getPref()));
				break;
			case R.id.run:
				startActivity(RunningActivity.newIntent(getActivity(), testSuite, null));
				break;
			default:
				startActivity(OverviewActivity.newIntent(getActivity(), testSuite));
				break;
		}
	}
}
