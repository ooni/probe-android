package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementPsiphonBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

import io.noties.markwon.Markwon;

public class PsiphonFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static PsiphonFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		PsiphonFragment fragment = new PsiphonFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementPsiphonBinding binding = FragmentMeasurementPsiphonBinding.inflate(inflater,container,false);
		Markwon.builder(getContext())
				.build()
				.setMarkdown(binding.desc,
				measurement.is_anomaly ?
						getString(R.string.TestResults_Details_Circumvention_Psiphon_Blocked_Content_Paragraph) :
						getString(R.string.TestResults_Details_Circumvention_Psiphon_Reachable_Content_Paragraph)
		);
		binding.bootstrap.setText(measurement.getTestKeys().getBootstrapTime(getActivity()));
		return binding.getRoot();
	}
}
