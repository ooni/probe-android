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
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementHeaderNdtBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;

public class HeaderNdtFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static HeaderNdtFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		HeaderNdtFragment fragment = new HeaderNdtFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementHeaderNdtBinding binding = FragmentMeasurementHeaderNdtBinding.inflate(inflater,container,false);
		binding.download.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getDownload(getActivity()), getString(measurement.getTestKeys().getDownloadUnit()))));
		binding.upload.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getUpload(getActivity()), getString(measurement.getTestKeys().getUploadUnit()))));
		binding.ping.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getPing(getActivity()), "ms")));
		binding.server.setText(measurement.getTestKeys().getServerDetails(getActivity()));
		return binding.getRoot();
	}
}
