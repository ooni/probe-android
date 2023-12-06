package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementRiseupvpnBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

import io.noties.markwon.Markwon;

public class RiseupVPNFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

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
		FragmentMeasurementRiseupvpnBinding binding = FragmentMeasurementRiseupvpnBinding.inflate(inflater,container,false);
		Markwon.builder(getContext())
				.build()
				.setMarkdown(binding.desc,
				measurement.is_anomaly ?
						getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Blocked_Content_Paragraph) :
						getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Content_Paragraph)
		);
		binding.bootstrapValue.setText(measurement.getTestKeys().getRiseupVPNApiStatus());
		binding.openvpnValue.setText(measurement.getTestKeys().getRiseupVPNOpenvpnGatewayStatus(getContext()));
		binding.bridgesValue.setText(measurement.getTestKeys().getRiseupVPNBridgedGatewayStatus(getContext()));
		return binding.getRoot();
	}
}
