package org.openobservatory.ooniprobe.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultHeaderDetailFragment;
import org.openobservatory.ooniprobe.fragment.ResultHeaderMiddleboxFragment;
import org.openobservatory.ooniprobe.fragment.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.fragment.ResultHeaderTBAFragment;
import org.openobservatory.ooniprobe.item.MeasurementItem;
import org.openobservatory.ooniprobe.item.MeasurementPerfItem;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Network;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Result_Table;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class ResultDetailActivity extends AbstractActivity implements View.OnClickListener {
	public static final String ID = "id";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.tabLayout) TabLayout tabLayout;
	@BindView(R.id.pager) ViewPager pager;
	@BindView(R.id.recyclerView) RecyclerView recycler;
	private Result result;

	public static Intent newIntent(Context context, int id) {
		return new Intent(context, ResultDetailActivity.class).putExtra(ID, id);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		result = SQLite.select().from(Result.class).where(Result_Table.id.eq(getIntent().getIntExtra(ID, 0))).querySingle();
		assert result != null;
		setTheme(result.getTestSuite().getThemeLight());
		setContentView(R.layout.activity_result_detail);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
		}
		pager.setAdapter(new ResultHeaderAdapter());
		tabLayout.setupWithViewPager(pager);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
		ArrayList<HeterogeneousRecyclerItem> items = new ArrayList<>();
		boolean isPerf = result.test_group_name.equals(PerformanceSuite.NAME);
		for (Measurement measurement : result.getMeasurements())
			items.add(isPerf ? new MeasurementPerfItem(measurement, this) : new MeasurementItem(measurement, this));
		recycler.setAdapter(new HeterogeneousRecyclerAdapter<>(this, items));
	}

	@Override public void onClick(View v) {
		Measurement measurement = (Measurement) v.getTag();
		startActivity(MeasurementDetailActivity.newIntent(this, measurement.id));
	}

	private class ResultHeaderAdapter extends FragmentPagerAdapter {
		ResultHeaderAdapter() {
			super(getFragmentManager());
		}

		@Override public Fragment getItem(int position) {
			if (position == 1) {
				Network network = result.getMeasurement().network;
				return ResultHeaderDetailFragment.newInstance(0L, 0L, null, result.runtime, true, network.country_code, network.network_name);
			} else switch (result.test_group_name) {
				case WebsitesSuite.NAME:
					return ResultHeaderTBAFragment.newInstance(result, R.plurals.TestResults_Summary_Websites_Hero_Sites);
				case InstantMessagingSuite.NAME:
					return ResultHeaderTBAFragment.newInstance(result, R.plurals.TestResults_Summary_InstantMessaging_Hero_Apps);
				case MiddleBoxesSuite.NAME:
					return ResultHeaderMiddleboxFragment.newInstance(result.countMeasurement(true, null) > 0);
				case PerformanceSuite.NAME:
					return ResultHeaderPerformanceFragment.newInstance(result);
				default:
					return null;
			}
		}

		@Override public int getCount() {
			return 2;
		}

		@Nullable @Override public CharSequence getPageTitle(int position) {
			return "●";
		}
	}
}
