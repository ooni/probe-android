package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementDashBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class DashFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static DashFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		DashFragment fragment = new DashFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementDashBinding binding = FragmentMeasurementDashBinding.inflate(inflater,container,false);
		binding.medianBitrate.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getMedianBitrate(getActivity()), getString(measurement.getTestKeys().getMedianBitrateUnit()))));
		binding.playoutDelay.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getPlayoutDelay(getActivity()), "s")));
		return binding.getRoot();
	}
}
