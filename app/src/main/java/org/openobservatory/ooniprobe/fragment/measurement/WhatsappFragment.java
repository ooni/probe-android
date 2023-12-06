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
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementWhatsappBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

public class WhatsappFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static WhatsappFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		WhatsappFragment fragment = new WhatsappFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementWhatsappBinding binding = FragmentMeasurementWhatsappBinding.inflate(inflater,container,false);
		binding.desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_WhatsApp_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_WhatsApp_Reachable_Content_Paragraph);
		binding.application.setText(measurement.getTestKeys().getWhatsappEndpointStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().whatsapp_endpoints_status))
			binding.application.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		binding.webApp.setText(measurement.getTestKeys().getWhatsappWebStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().whatsapp_web_status))
			binding.webApp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		binding.registrations.setText(measurement.getTestKeys().getWhatsappRegistrationStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().registration_server_status))
			binding.registrations.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		return binding.getRoot();
	}
}
