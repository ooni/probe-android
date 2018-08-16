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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.item.DateItem;
import org.openobservatory.ooniprobe.item.InstantMessagingItem;
import org.openobservatory.ooniprobe.item.MiddleboxesItem;
import org.openobservatory.ooniprobe.item.PerformanceItem;
import org.openobservatory.ooniprobe.item.WebsiteItem;
import org.openobservatory.ooniprobe.model.Network;
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
	private ArrayList<HeterogeneousRecyclerItem> items;
	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.TestResults_Overview_Title);
		tests.setText(getString(R.string.decimal, SQLite.selectCountOf().from(Result.class).longValue()));
		networks.setText(getString(R.string.decimal, SQLite.selectCountOf().from(Network.class).longValue()));
		upload.setText(getString(R.string.decimal, SQLite.select(Method.sum(Result_Table.data_usage_up)).from(Result.class).longValue()));
		download.setText(getString(R.string.decimal, SQLite.select(Method.sum(Result_Table.data_usage_down)).from(Result.class).longValue()));
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
		items = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		return v;
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.delete, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void queryList(String nameFilter) {
		HashSet<Integer> set = new HashSet<>();
		items.clear();
		ArrayList<SQLOperator> where = new ArrayList<>();
		if (nameFilter != null && !nameFilter.isEmpty())
			where.add(Result_Table.test_group_name.is(nameFilter));
		for (Result result : SQLite.select().from(Result.class).where(where.toArray(new SQLOperator[where.size()])).orderBy(Result_Table.start_time, false).queryList()) {
			Calendar c = Calendar.getInstance();
			c.setTime(result.start_time);
			int key = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
			if (!set.contains(key)) {
				items.add(new DateItem(result.start_time));
				set.add(key);
			}
			switch (result.test_group_name) {
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
		adapter.notifyTypesChanged();
	}

	@OnItemSelected(R.id.filterTests) public void filterTestsItemSelected(int pos) {
		queryList(getResources().getStringArray(R.array.filterTestValues)[pos]);
	}
}
