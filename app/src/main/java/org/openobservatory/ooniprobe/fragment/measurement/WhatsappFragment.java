package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WhatsappFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.desc) TextView desc;
	@BindView(R.id.application) TextView application;
	@BindView(R.id.webApp) TextView webApp;
	@BindView(R.id.registrations) TextView registrations;

	public static WhatsappFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		WhatsappFragment fragment = new WhatsappFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_whatsapp, container, false);
		ButterKnife.bind(this, v);
		desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_WhatsApp_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_WhatsApp_Reachable_Content_Paragraph);
		application.setText(measurement.getTestKeys().getWhatsappEndpointStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().whatsapp_endpoints_status))
			application.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		webApp.setText(measurement.getTestKeys().getWhatsappWebStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().whatsapp_web_status))
			webApp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		registrations.setText(measurement.getTestKeys().getWhatsappRegistrationStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().registration_server_status))
			registrations.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		return v;
	}
}
