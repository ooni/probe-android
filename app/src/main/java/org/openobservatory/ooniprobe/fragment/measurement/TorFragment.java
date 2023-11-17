package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementTorBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

import io.noties.markwon.Markwon;

public class TorFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static TorFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		TorFragment fragment = new TorFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementTorBinding binding = FragmentMeasurementTorBinding.inflate(inflater,container,false);
		Markwon.builder(getContext())
				.build()
				.setMarkdown(binding.desc,
				measurement.is_anomaly ?
						getString(R.string.TestResults_Details_Circumvention_Tor_Blocked_Content_Paragraph) :
						getString(R.string.TestResults_Details_Circumvention_Tor_Reachable_Content_Paragraph)
		);
		binding.bridges.setText(measurement.getTestKeys().getBridges(getActivity()));
		binding.authorities.setText(measurement.getTestKeys().getAuthorities(getActivity()));
		return binding.getRoot();
	}
}
