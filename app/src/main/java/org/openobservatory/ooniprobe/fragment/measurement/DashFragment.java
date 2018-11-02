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

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		View v = inflater.inflate(R.layout.fragment_measurement_dash, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (testKeys != null) {
			medianBitrate.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getMedianBitrate(getActivity()), getString(testKeys.getMedianBitrateUnit()))));
			playoutDelay.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getPlayoutDelay(getActivity()), "s")));
		}
		return v;
	}
}
