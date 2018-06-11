package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;
import org.openobservatory.ooniprobe.item.TestItem;
import org.openobservatory.ooniprobe.model.Test;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;

public class DashboardFragment extends Fragment implements View.OnClickListener {
	@BindView(R.id.recycler) RecyclerView recycler;
	private ArrayList<TestItem> items;
	private HeterogeneousRecyclerAdapter<TestItem> adapter;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.recycler, container, false);
		ButterKnife.bind(this, v);
		items = new ArrayList<>();
		items.add(new TestItem(Test.getWebsiteTest(), this));
		items.add(new TestItem(Test.getInstantMessaging(), this));
		items.add(new TestItem(Test.getMiddleBoxes(), this));
		items.add(new TestItem(Test.getPerformance(), this));
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		return v;
	}

	@Override public void onClick(View v) {
		Test test = (Test) v.getTag();
		switch (test.getTitle()) {
			case R.string.Test_Websites_Fullname:
				switch (v.getId()) {
					case R.id.configure:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_websites));
						break;
					case R.id.run:
						break;
				}
				break;
			case R.string.Test_InstantMessaging_Fullname:
				switch (v.getId()) {
					case R.id.configure:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_instant_messaging));
						break;
					case R.id.run:
						break;
				}
				break;
			case R.string.Test_Middleboxes_Fullname:
				switch (v.getId()) {
					case R.id.configure:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_middleboxes));
						break;
					case R.id.run:
						break;
				}
				break;
			case R.string.Test_Performance_Fullname:
				switch (v.getId()) {
					case R.id.configure:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_performance));
						break;
					case R.id.run:
						break;
				}
				break;
		}
	}
}
