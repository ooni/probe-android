package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.question_mark, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_red8));
			title.setText(R.string.TestResults_Details_Websites_LikelyBlocked_Hero_Title);
			/* TODO strings
			HashMap<String, String> data = new HashMap<>();
			data.put("WebsiteURL", measurement.url.url);
			data.put("BlockingReason", testKeys.getWebsiteBlocking(getActivity()));
			desc.setText(Mustache.compiler().compile(getString(R.string.TestResults_Details_Websites_LikelyBlocked_Content_Paragraph)).execute(data));
			*/
		} else {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tick, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_green8));
			title.setText(R.string.TestResults_Details_Websites_Reachable_Hero_Title);
			/* TODO strings
			HashMap<String, String> data = new HashMap<>();
			data.put("WebsiteURL", measurement.url.url);
			desc.setText(Mustache.compiler().compile(getString(R.string.TestResults_Details_Websites_Reachable_Content_Paragraph)).execute(data));
			*/
		}
		return v;
	}
}
