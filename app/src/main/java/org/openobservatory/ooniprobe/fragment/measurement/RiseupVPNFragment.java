package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.markwon.Markwon;

public class RiseupVPNFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	@BindView(R.id.bootstrap_value) TextView bootstrap_value;
	@BindView(R.id.openvpn_value) TextView openvpn_value;
	@BindView(R.id.bridges_value) TextView bridges_value;
	@BindView(R.id.desc) TextView desc;

	public static RiseupVPNFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		RiseupVPNFragment fragment = new RiseupVPNFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_riseupvpn, container, false);
		ButterKnife.bind(this, v);
		Markwon.setMarkdown(desc,
				measurement.is_anomaly ?
						getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Blocked_Content_Paragraph) :
						getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Content_Paragraph)
		);
		bootstrap_value.setText(measurement.getTestKeys().getRiseupVPNApiStatus());
		openvpn_value.setText(measurement.getTestKeys().getRiseupVPNOpenvpnGatewayStatus(getContext()));
		bridges_value.setText(measurement.getTestKeys().getRiseupVPNBridgedGatewayStatus(getContext()));
		return v;
	}
}
