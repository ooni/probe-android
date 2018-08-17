package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Result_Table;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstantMessagingFragment extends Fragment {
	public static final String ID = "id";
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.tabLayout) TabLayout tabLayout;
	@BindView(R.id.pager) ViewPager pager;
	private Result result;

	public static InstantMessagingFragment newInstance(int id) {
		Bundle args = new Bundle();
		args.putInt(ID, id);
		InstantMessagingFragment fragment = new InstantMessagingFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		result = SQLite.select().from(Result.class).where(Result_Table.id.eq(getArguments().getInt(ID))).querySingle();
		View v = inflater.inflate(R.layout.fragment_instantmessaging, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
		}
		pager.setAdapter(new ResultHeaderAdapter());
		tabLayout.setupWithViewPager(pager);
		return v;
	}

	private class ResultHeaderAdapter extends FragmentPagerAdapter {
		ResultHeaderAdapter() {
			super(getChildFragmentManager());
		}

		@Override public Fragment getItem(int position) {
			return position == 0 ? ResultHeaderTBAFragment.newInstance(result, getString(R.string.TestResults_Summary_InstantMessaging_Hero_Apps_Plural)) : ResultHeaderDetailFragment.newInstance(result);
		}

		@Override public int getCount() {
			return 2;
		}

		@Nullable @Override public CharSequence getPageTitle(int position) {
			return position == 0 ? "sum" : "detail";
		}
	}
}
