package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementNdtBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class NdtFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static NdtFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		NdtFragment fragment = new NdtFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementNdtBinding binding = FragmentMeasurementNdtBinding.inflate(inflater,container,false);
		binding.packetLoss.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getPacketLoss(getActivity()), "%")));
		binding.averagePing.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getAveragePing(getActivity()), "ms")));
		binding.maxPing.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getMaxPing(getActivity()), "ms")));
		binding.mss.setText(measurement.getTestKeys().getMSS(getActivity()));
		return binding.getRoot();
	}
}
