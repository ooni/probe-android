package org.openobservatory.ooniprobe.fragment.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.samskivert.mustache.Mustache;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.TestKeys;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebConnectivityFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.title) TextView title;
	@BindView(R.id.desc) TextView desc;

	public static WebConnectivityFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		WebConnectivityFragment fragment = new WebConnectivityFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_webconnectivity, container, false);
		ButterKnife.bind(this, v);
		TestKeys testKeys = measurement.getTestKeys();
		if (measurement.is_anomaly) {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.cross, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_red8));
			title.setText(R.string.TestResults_Details_Websites_LikelyBlocked_Hero_Title);
			HashMap<String, String> data = new HashMap<>();
			data.put("WebsiteURL", measurement.url.url);
			data.put("BlockingReason", testKeys.getWebsiteBlocking(getActivity()));
			desc.setText(Mustache.compiler().compile(getString(R.string.TestResults_Details_Websites_LikelyBlocked_Content_Paragraph_1)).execute(data));
		} else {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tick, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_green7));
			title.setText(R.string.TestResults_Details_Websites_Reachable_Hero_Title);
			HashMap<String, String> data = new HashMap<>();
			data.put("WebsiteURL", measurement.url.url);
			desc.setText(Mustache.compiler().compile(getString(R.string.TestResults_Details_Websites_Reachable_Content_Paragraph_1)).execute(data));
		}
		return v;
	}
}
