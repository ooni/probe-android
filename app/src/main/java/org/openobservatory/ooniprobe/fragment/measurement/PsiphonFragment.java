package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PsiphonFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	@BindView(R.id.bootstrap) TextView bootstrap;
	@BindView(R.id.desc) TextView desc;

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
		View v = inflater.inflate(R.layout.fragment_measurement_psiphon, container, false);
		ButterKnife.bind(this, v);
		desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_Circumvention_Psiphon_Blocked_Content_Paragraph : R.string.TestResults_Details_Circumvention_Psiphon_Reachable_Content_Paragraph);
		/*
		TestResults_Details_Circumvention_Psiphon_BootstrapTime_Label_Unit
		dns.setText(measurement.getTestKeys().getFacebookMessengerDns());
		if (Boolean.TRUE.equals(measurement.getTestKeys().facebook_dns_blocking))
			dns.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		tcp.setText(measurement.getTestKeys().getFacebookMessengerTcp());
		if (Boolean.TRUE.equals(measurement.getTestKeys().facebook_tcp_blocking))
			tcp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		 */
		return v;
	}
}
