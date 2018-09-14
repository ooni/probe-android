package org.openobservatory.ooniprobe.fragment.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WhatsappFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.title) TextView title;
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

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_whatsapp, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (measurement.is_anomaly) {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.cross, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_red8));
			title.setText(R.string.TestResults_Details_InstantMessaging_WhatsApp_LikelyBlocked_Hero_Title);
			desc.setText(R.string.TestResults_Details_InstantMessaging_WhatsApp_LikelyBlocked_Content_Paragraph_1);
		} else {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tick, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_green7));
			title.setText(R.string.TestResults_Details_InstantMessaging_WhatsApp_Reachable_Hero_Title);
			desc.setText(R.string.TestResults_Details_InstantMessaging_WhatsApp_Reachable_Content_Paragraph_1);
		}
		if (testKeys != null) {
			application.setText(testKeys.getWhatsappEndpointStatus(getActivity()));
			webApp.setText(testKeys.getWhatsappWebStatus(getActivity()));
			registrations.setText(testKeys.getWhatsappRegistrationStatus(getActivity()));
		}
		return v;
	}
}
