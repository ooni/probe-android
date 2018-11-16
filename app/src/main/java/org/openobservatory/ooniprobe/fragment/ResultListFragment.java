package org.openobservatory.ooniprobe.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.item.DateItem;
import org.openobservatory.ooniprobe.item.InstantMessagingItem;
import org.openobservatory.ooniprobe.item.MiddleboxesItem;
import org.openobservatory.ooniprobe.item.PerformanceItem;
import org.openobservatory.ooniprobe.item.WebsiteItem;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import localhost.toolkit.app.ConfirmDialogFragment;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class ResultListFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, ConfirmDialogFragment.OnConfirmedListener {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.tests) TextView tests;
	@BindView(R.id.networks) TextView networks;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.filterTests) Spinner filterTests;
	@BindView(R.id.recycler) RecyclerView recycler;
	@BindView(R.id.emptyState) TextView emptyState;
	private ArrayList<HeterogeneousRecyclerItem> items;
	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;
	private boolean refresh;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_list, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.TestResults_Overview_Title);
		tests.setText(getString(R.string.d, SQLite.selectCountOf().from(Result.class).longValue()));
		networks.setText(getString(R.string.d, SQLite.selectCountOf().from(Network.class).longValue()));
		upload.setText(Result.readableFileSize(SQLite.select(Method.sum(Result_Table.data_usage_up)).from(Result.class).longValue()));
		download.setText(Result.readableFileSize(SQLite.select(Method.sum(Result_Table.data_usage_down)).from(Result.class).longValue()));
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
		items = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		return v;
	}

	@Override public void onResume() {
		super.onResume();
		if (refresh) {
			queryList();
			refresh = false;
		}
	}

	@Override public void onPause() {
		super.onPause();
		refresh = true;
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.delete, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete:
				ConfirmDialogFragment.newInstance(null, getString(R.string.General_AppName), getString(R.string.Modal_DoYouWantToDeleteAllTests), null, getString(R.string.Modal_Delete), null, null).show(getChildFragmentManager(), null);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@OnItemSelected(R.id.filterTests) void queryList() {
		HashSet<Integer> set = new HashSet<>();
		items.clear();
		ArrayList<SQLOperator> where = new ArrayList<>();
		String filter = getResources().getStringArray(R.array.filterTestValues)[filterTests.getSelectedItemPosition()];
		if (!filter.isEmpty())
			where.add(Result_Table.test_group_name.is(filter));
		List<Result> list = SQLite.select().from(Result.class).where(where.toArray(new SQLOperator[0])).orderBy(Result_Table.start_time, false).queryList();
		if (list.isEmpty()) {
			emptyState.setVisibility(View.VISIBLE);
			recycler.setVisibility(View.GONE);
		} else {
			emptyState.setVisibility(View.GONE);
			recycler.setVisibility(View.VISIBLE);
			for (Result result : list) {
				Calendar c = Calendar.getInstance();
				c.setTime(result.start_time);
				int key = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
				if (!set.contains(key)) {
					items.add(new DateItem(result.start_time));
					set.add(key);
				}
				switch (result.test_group_name) {
					case WebsitesSuite.NAME:
						items.add(new WebsiteItem(result, this, this));
						break;
					case InstantMessagingSuite.NAME:
						items.add(new InstantMessagingItem(result, this, this));
						break;
					case MiddleBoxesSuite.NAME:
						items.add(new MiddleboxesItem(result, this, this));
						break;
					case PerformanceSuite.NAME:
						items.add(new PerformanceItem(result, this, this));
						break;
				}
			}
			adapter.notifyTypesChanged();
		}
	}

	@Override public void onClick(View v) {
		Result result = (Result) v.getTag();
		ActivityCompat.startActivity(getActivity(), ResultDetailActivity.newIntent(getActivity(), result.id),null /* ActivityOptionsCompat.makeClipRevealAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle()*/);
	}

	@Override public boolean onLongClick(View v) {
		Result result = (Result) v.getTag();
		ConfirmDialogFragment.newInstance(result, getString(R.string.General_AppName), getString(R.string.Modal_DoYouWantToDeleteThisTest), null, getString(R.string.Modal_Delete), null, null).show(getChildFragmentManager(), null);
		return true;
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_POSITIVE) {
			if (serializable == null) {
				Result.deleteAll(getActivity());
			} else {
				((Result) serializable).delete(getActivity());
			}
			queryList();
		}
	}
}
