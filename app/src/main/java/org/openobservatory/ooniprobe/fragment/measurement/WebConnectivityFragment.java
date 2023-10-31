package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementWebconnectivityBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import ru.noties.markwon.Markwon;

public class WebConnectivityFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static WebConnectivityFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		WebConnectivityFragment fragment = new WebConnectivityFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementWebconnectivityBinding binding = FragmentMeasurementWebconnectivityBinding.inflate(inflater,container,false);
		if (measurement.is_anomaly)
			Markwon.setMarkdown(binding.desc, getString(R.string.TestResults_Details_Websites_LikelyBlocked_Content_Paragraph, measurement.url.url, getString(measurement.getTestKeys().getWebsiteBlocking())));
		else
			Markwon.setMarkdown(binding.desc, getString(R.string.TestResults_Details_Websites_Reachable_Content_Paragraph, measurement.url.url));
		return binding.getRoot();
	}
}
