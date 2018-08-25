package org.openobservatory.ooniprobe.fragment.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;

import butterknife.ButterKnife;

public class WebConnectivityFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";

	public static WebConnectivityFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		WebConnectivityFragment fragment = new WebConnectivityFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		View v = inflater.inflate(R.layout.fragment_measurement_webconnectivity, container, false);
		ButterKnife.bind(this, v);
		return v;
	}
}
