package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderDetailFragment extends Fragment {
	public static final String NETWORK_NAME = "network_name";
	public static final String COUNTRY_CODE = "country_code";
	public static final String RUNTIME = "runtime";
	public static final String DATA_USAGE_DOWN = "data_usage_down";
	public static final String DATA_USAGE_UP = "data_usage_up";
	public static final String START_TIME = "start_time";
	public static final String IS_TOTAL_RUNTIME = "isTotalRuntime";
	@BindView(R.id.dataUsage) LinearLayout dataUsage;
	@BindView(R.id.dateTime) LinearLayout dateTime;
	@BindView(R.id.startTime) TextView startTime;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.runtime) TextView runtime;
	@BindView(R.id.runtimeLabel) TextView runtimeLabel;
	@BindView(R.id.country) TextView country;
	@BindView(R.id.network) TextView network;

	public static ResultHeaderDetailFragment newInstance(Long data_usage_up, Long data_usage_down, Date start_time, double runtime, boolean isTotalRuntime, String country_code, String network_name) {
		Bundle args = new Bundle();
		if (data_usage_up != null && data_usage_down != null) {
			args.putLong(DATA_USAGE_UP, data_usage_up);
			args.putLong(DATA_USAGE_DOWN, data_usage_down);
		}
		if (start_time != null)
			args.putSerializable(START_TIME, start_time);
		args.putDouble(RUNTIME, runtime);
		args.putBoolean(IS_TOTAL_RUNTIME, isTotalRuntime);
		args.putString(COUNTRY_CODE, country_code);
		args.putString(NETWORK_NAME, network_name);
		ResultHeaderDetailFragment fragment = new ResultHeaderDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_head_detail, container, false);
		ButterKnife.bind(this, v);
		if (getArguments().containsKey(DATA_USAGE_DOWN) && getArguments().containsKey(DATA_USAGE_UP)) {
			download.setText(getArguments().getLong(DATA_USAGE_DOWN) + "");
			upload.setText(getArguments().getLong(DATA_USAGE_UP) + "");
		} else
			dataUsage.setVisibility(View.GONE);
		if (getArguments().containsKey(START_TIME))
			startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), (Date) getArguments().getSerializable(START_TIME)));
		else
			dateTime.setVisibility(View.GONE);
		runtime.setText(getString(R.string.f, getArguments().getDouble(RUNTIME)));
		country.setText(getArguments().getString(COUNTRY_CODE));
		network.setText(getArguments().getString(NETWORK_NAME));
		runtimeLabel.setText(getArguments().getBoolean(IS_TOTAL_RUNTIME)? R.string.TestResults_Summary_Hero_Runtime: R.string.TestResults_Details_Hero_Runtime);
		return v;
	}
}
