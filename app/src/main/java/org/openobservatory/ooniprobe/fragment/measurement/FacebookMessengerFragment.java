package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementFacebookmessengerBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class FacebookMessengerFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	public static FacebookMessengerFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		FacebookMessengerFragment fragment = new FacebookMessengerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementFacebookmessengerBinding binding = FragmentMeasurementFacebookmessengerBinding.inflate(inflater,container,false);
		binding.desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_FacebookMessenger_Reachable_Content_Paragraph);
		binding.dns.setText(measurement.getTestKeys().getFacebookMessengerDns());
		if (Boolean.TRUE.equals(measurement.getTestKeys().facebook_dns_blocking))
			binding.dns.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		binding.tcp.setText(measurement.getTestKeys().getFacebookMessengerTcp());
		if (Boolean.TRUE.equals(measurement.getTestKeys().facebook_tcp_blocking))
			binding.tcp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		return binding.getRoot();
	}
}
