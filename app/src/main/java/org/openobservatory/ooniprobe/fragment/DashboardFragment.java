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
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;
import org.openobservatory.ooniprobe.item.TestItem;
import org.openobservatory.ooniprobe.model.Test;
import org.openobservatory.ooniprobe.tests.NetworkTest;

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
		switch (v.getId()) {
			case R.id.configure:
				switch (test.getTitle()) {
					case R.string.Test_Websites_Fullname:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_websites));
						break;
					case R.string.Test_InstantMessaging_Fullname:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_instant_messaging));
						break;
					case R.string.Test_Middleboxes_Fullname:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_middleboxes));
						break;
					case R.string.Test_Performance_Fullname:
						startActivity(PreferenceActivity.newIntent(getActivity(), R.xml.preferences_performance));
						break;
				}
				break;
			case R.id.run:
				switch (test.getTitle()) {
					case R.string.Test_Websites_Fullname:
						NetworkTest.WCNetworkTest wcTest = new NetworkTest.WCNetworkTest(getActivity());
						wcTest.run();
						break;
					case R.string.Test_InstantMessaging_Fullname:
						NetworkTest.IMNetworkTest imTest = new NetworkTest.IMNetworkTest(getActivity());
						imTest.run();
						break;
					case R.string.Test_Middleboxes_Fullname:
						NetworkTest.MBNetworkTest mbTest = new NetworkTest.MBNetworkTest(getActivity());
						mbTest.run();
						break;
					case R.string.Test_Performance_Fullname:
						NetworkTest.SPNetworkTest spTest = new NetworkTest.SPNetworkTest(getActivity());
						spTest.run();
						break;
				}
				break;
			default:
				startActivity(OverviewActivity.newIntent(getActivity(), test));
				break;
		}
	}
}
