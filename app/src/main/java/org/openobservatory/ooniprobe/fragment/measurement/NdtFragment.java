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

public class NdtFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.packetLoss) TextView packetLoss;
	@BindView(R.id.outOfOrder) TextView outOfOrder;
	@BindView(R.id.averagePing) TextView averagePing;
	@BindView(R.id.maxPing) TextView maxPing;
	@BindView(R.id.mss) TextView mss;
	@BindView(R.id.timeouts) TextView timeouts;

	public static NdtFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		NdtFragment fragment = new NdtFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_ndt, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (testKeys != null) {
			packetLoss.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getPacketLoss(getActivity()), "%")));
			outOfOrder.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getOutOfOrder(getActivity()), "%")));
			averagePing.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getAveragePing(getActivity()), "ms")));
			maxPing.setText(Html.fromHtml(getString(R.string.bigNormal, testKeys.getMaxPing(getActivity()), "ms")));
			mss.setText(testKeys.getMSS(getActivity()));
			timeouts.setText(testKeys.getTimeouts(getActivity()));
		}
		return v;
	}
}
