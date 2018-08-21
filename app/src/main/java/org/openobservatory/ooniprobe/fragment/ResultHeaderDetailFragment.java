package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderDetailFragment extends Fragment {
	public static final String NETWORK_NAME = "network_name";
	public static final String COUNTRY_CODE = "country_code";
	public static final String RUNTIME = "runtime";
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.runtime) TextView runtime;
	@BindView(R.id.country) TextView country;
	@BindView(R.id.network) TextView network;

	public static ResultHeaderDetailFragment newInstance(float runtime, String country_code, String network_name) {
		Bundle args = new Bundle();
		args.putFloat(RUNTIME, runtime);
		args.putString(COUNTRY_CODE, country_code);
		args.putString(NETWORK_NAME, network_name);
		ResultHeaderDetailFragment fragment = new ResultHeaderDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_head_detail, container, false);
		ButterKnife.bind(this, v);
		runtime.setText(getString(R.string.f, getArguments().getFloat(RUNTIME)));
		country.setText(getArguments().getString(COUNTRY_CODE));
		network.setText(getArguments().getString(NETWORK_NAME));
		return v;
	}
}
