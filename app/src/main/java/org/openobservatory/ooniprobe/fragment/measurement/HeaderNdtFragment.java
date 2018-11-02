package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderNdtFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
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

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_header_ndt, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (testKeys != null) {
			download.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getDownload(getActivity()), getString(testKeys.getDownloadUnit()))));
			upload.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getUpload(getActivity()), getString(testKeys.getUploadUnit()))));
			ping.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getPing(getActivity()), "ms")));
			server.setText(testKeys.getServer(getActivity()));
		}
		return v;
	}
}
