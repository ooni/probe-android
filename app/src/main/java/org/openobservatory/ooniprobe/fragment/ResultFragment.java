package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.item.DateItem;
import org.openobservatory.ooniprobe.item.InstantMessagingItem;
import org.openobservatory.ooniprobe.item.MiddleboxesItem;
import org.openobservatory.ooniprobe.item.PerformanceItem;
import org.openobservatory.ooniprobe.item.WebsiteItem;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Result_Table;
import org.openobservatory.ooniprobe.test.TestSuite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class ResultFragment extends Fragment {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.tests) TextView tests;
	@BindView(R.id.networks) TextView networks;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.recycler) RecyclerView recycler;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		getActivity().setTitle(R.string.TestResults_Overview_Title);
		tests.setText(getString(R.string.decimal, SQLite.selectCountOf().from(Result.class).longValue()));
		networks.setText(getString(R.string.decimal, SQLite.selectCountOf(Result_Table.asn.distinct()).from(Result.class).longValue()));
		upload.setText(getString(R.string.decimal, SQLite.select(Method.sum(Result_Table.dataUsageUp)).from(Result.class).longValue()));
		download.setText(getString(R.string.decimal, SQLite.select(Method.sum(Result_Table.dataUsageDown)).from(Result.class).longValue()));
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
		ArrayList<HeterogeneousRecyclerItem> items = new ArrayList<>();
		HashSet<Integer> set = new HashSet<>();
		for (Result result : SQLite.select().from(Result.class).orderBy(Result_Table.startTime, false).queryList()) {
			Calendar c = Calendar.getInstance();
			c.setTime(result.startTime);
			int key = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
			if (!set.contains(key)) {
				items.add(new DateItem(result.startTime));
				set.add(key);
			}
			switch (result.name) {
				case TestSuite.WEBSITES:
					items.add(new WebsiteItem(result));
					break;
				case TestSuite.INSTANT_MESSAGING:
					items.add(new InstantMessagingItem(result));
					break;
				case TestSuite.MIDDLE_BOXES:
					items.add(new MiddleboxesItem(result));
					break;
				case TestSuite.PERFORMANCE:
					items.add(new PerformanceItem(result));
					break;
			}
		}
		HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		return v;
	}

	@OnItemSelected(R.id.filterTests) public void filterTestsItemSelected(int pos) {
	}
}
