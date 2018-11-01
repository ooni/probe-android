package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
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
	public static final String LIGHT_THEME = "lightTheme";
	@BindView(R.id.dataUsage) LinearLayout dataUsage;
	@BindView(R.id.startTimeBox) LinearLayout startTimeBox;
	@BindView(R.id.runtimeBox) LinearLayout runtimeBox;
	@BindView(R.id.countryBox) LinearLayout countryBox;
	@BindView(R.id.networkBox) LinearLayout networkBox;
	@BindView(R.id.startTime) TextView startTime;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.runtime) TextView runtime;
	@BindView(R.id.runtimeLabel) TextView runtimeLabel;
	@BindView(R.id.country) TextView country;
	@BindView(R.id.network) TextView network;

	public static ResultHeaderDetailFragment newInstance(boolean lightTheme, String data_usage_up, String data_usage_down, Date start_time, Double runtime, Boolean isTotalRuntime, String country_code, String network_name) {
		Bundle args = new Bundle();
		args.putBoolean(LIGHT_THEME, lightTheme);
		if (data_usage_up != null && data_usage_down != null) {
			args.putString(DATA_USAGE_UP, data_usage_up);
			args.putString(DATA_USAGE_DOWN, data_usage_down);
		}
		if (start_time != null)
			args.putSerializable(START_TIME, start_time);
		if (runtime != null)
			args.putDouble(RUNTIME, runtime);
		if (isTotalRuntime != null)
			args.putBoolean(IS_TOTAL_RUNTIME, isTotalRuntime);
		if (country_code != null)
			args.putString(COUNTRY_CODE, country_code);
		if (network_name != null)
			args.putString(NETWORK_NAME, network_name);
		ResultHeaderDetailFragment fragment = new ResultHeaderDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.cloneInContext(new ContextThemeWrapper(getActivity(),
				getArguments().getBoolean(LIGHT_THEME) ? R.style.Theme_MaterialComponents_Light_NoActionBar_App : R.style.Theme_MaterialComponents_NoActionBar_App)).inflate(R.layout.fragment_result_head_detail, container, false);
		ButterKnife.bind(this, v);
		if (getArguments().containsKey(DATA_USAGE_DOWN) && getArguments().containsKey(DATA_USAGE_UP)) {
			download.setText(getArguments().getString(DATA_USAGE_DOWN));
			upload.setText(getArguments().getString(DATA_USAGE_UP));
		} else
			dataUsage.setVisibility(View.GONE);
		if (getArguments().containsKey(START_TIME))
			startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), (Date) getArguments().getSerializable(START_TIME)));
		else
			startTimeBox.setVisibility(View.GONE);
		if (getArguments().containsKey(RUNTIME)) {
			runtime.setText(getString(R.string.f, getArguments().getDouble(RUNTIME)));
			runtimeLabel.setText(getArguments().getBoolean(IS_TOTAL_RUNTIME) ? R.string.TestResults_Summary_Hero_Runtime : R.string.TestResults_Details_Hero_Runtime);
		} else
			runtimeBox.setVisibility(View.GONE);
		if (getArguments().containsKey(COUNTRY_CODE))
			country.setText(getArguments().getString(COUNTRY_CODE));
		else
			countryBox.setVisibility(View.GONE);
		if (getArguments().containsKey(NETWORK_NAME))
			network.setText(Html.fromHtml(getArguments().getString(NETWORK_NAME)));
		else
			networkBox.setVisibility(View.GONE);
		return v;
	}
}
