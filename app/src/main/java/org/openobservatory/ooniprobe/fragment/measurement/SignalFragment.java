package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementSignalBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class SignalFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static SignalFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		SignalFragment fragment = new SignalFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementSignalBinding binding = FragmentMeasurementSignalBinding.inflate(inflater,container,false);
		binding.desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_Signal_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_Signal_Reachable_Content_Paragraph);
		return binding.getRoot();
	}
}
