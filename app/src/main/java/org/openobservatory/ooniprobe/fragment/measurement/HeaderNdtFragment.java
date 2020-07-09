package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderNdtFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	@BindView(R.id.download) TextView download;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.ping) TextView ping;
	@BindView(R.id.server) TextView server;

	public static HeaderNdtFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		HeaderNdtFragment fragment = new HeaderNdtFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_header_ndt, container, false);
		ButterKnife.bind(this, v);
		download.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getDownload(getActivity()), getString(measurement.getTestKeys().getDownloadUnit()))));
		upload.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getUpload(getActivity()), getString(measurement.getTestKeys().getUploadUnit()))));
		ping.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getPing(getActivity()), "ms")));
		server.setText(measurement.getTestKeys().getServerDetails(getActivity()));
		return v;
	}
}
