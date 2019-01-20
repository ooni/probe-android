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

public class DashFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.medianBitrate) TextView medianBitrate;
	@BindView(R.id.playoutDelay) TextView playoutDelay;

	public static DashFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		DashFragment fragment = new DashFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_dash, container, false);
		ButterKnife.bind(this, v);
		medianBitrate.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getMedianBitrate(getActivity()), getString(measurement.getTestKeys().getMedianBitrateUnit()))));
		playoutDelay.setText(Html.fromHtml(getString(R.string.bigNormal, measurement.getTestKeys().getPlayoutDelay(getActivity()), "s")));
		return v;
	}
}
