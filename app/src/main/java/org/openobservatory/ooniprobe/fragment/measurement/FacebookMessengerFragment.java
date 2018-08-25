package org.openobservatory.ooniprobe.fragment.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.TestKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacebookMessengerFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.title) TextView title;
	@BindView(R.id.desc) TextView desc;
	@BindView(R.id.tcp) TextView tcp;
	@BindView(R.id.dns) TextView dns;

	public static FacebookMessengerFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		FacebookMessengerFragment fragment = new FacebookMessengerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_facebookmessenger, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (measurement.is_anomaly) {
			title.setText(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_Hero_Title);
			desc.setText(testKeys.getFacebookMessengerBlocking(getActivity()));
		} else {
			title.setText(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_Reachable_Hero_Title);
			desc.setText(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_Reachable_Content_Paragraph_1);
		}
		if (testKeys != null) {
			dns.setText(testKeys.getFacebookMessengerDns(getActivity()));
			tcp.setText(testKeys.getFacebookMessengerTcp(getActivity()));
		}
		return v;
	}
}
