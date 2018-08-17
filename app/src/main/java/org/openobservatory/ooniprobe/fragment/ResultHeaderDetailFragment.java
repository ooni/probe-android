package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderDetailFragment extends Fragment {
	public static final String RESULT = "result";
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.runtime) TextView runtime;
	@BindView(R.id.country) TextView country;
	@BindView(R.id.network) TextView network;

	public static ResultHeaderDetailFragment newInstance(Result result) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		ResultHeaderDetailFragment fragment = new ResultHeaderDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_resultheader_detail, container, false);
		ButterKnife.bind(this, v);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		runtime.setText(getString(R.string.f, result.runtime));
		country.setText(result.getMeasurement().network.country_code);
		network.setText(result.getMeasurement().network.network_name);
		return v;
	}
}
