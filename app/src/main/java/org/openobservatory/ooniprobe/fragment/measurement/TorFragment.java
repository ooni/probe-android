package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.markwon.Markwon;

public class TorFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	@BindView(R.id.bridges) TextView bridges;
	@BindView(R.id.authorities) TextView authorities;
	@BindView(R.id.desc) TextView desc;

	public static TorFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		TorFragment fragment = new TorFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_tor, container, false);
		ButterKnife.bind(this, v);
		Markwon.setMarkdown(desc,
				measurement.is_anomaly ?
						getString(R.string.TestResults_Details_Circumvention_Tor_Blocked_Content_Paragraph) :
						getString(R.string.TestResults_Details_Circumvention_Tor_Reachable_Content_Paragraph)
		);
		bridges.setText(measurement.getTestKeys().getBridges(getActivity()));
		authorities.setText(measurement.getTestKeys().getAuthorities(getActivity()));
		for (TestKeys.TorTarget target : measurement.getTestKeys().targets)
			System.out.println("TORDEBUG "+ target.address + " connect " + target.connect + " handshake " + target.handshake);

		return v;
	}
}
