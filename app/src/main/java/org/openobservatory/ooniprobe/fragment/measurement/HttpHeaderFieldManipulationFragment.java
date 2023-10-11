package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementHttpheaderfieldmanipulationBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class HttpHeaderFieldManipulationFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static HttpHeaderFieldManipulationFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		HttpHeaderFieldManipulationFragment fragment = new HttpHeaderFieldManipulationFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementHttpheaderfieldmanipulationBinding binding = FragmentMeasurementHttpheaderfieldmanipulationBinding.inflate(inflater,container,false);
		binding.desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_Middleboxes_HTTPHeaderFieldManipulation_Found_Content_Paragraph : R.string.TestResults_Details_Middleboxes_HTTPHeaderFieldManipulation_NotFound_Content_Paragraph);
		return binding.getRoot();
	}
}
