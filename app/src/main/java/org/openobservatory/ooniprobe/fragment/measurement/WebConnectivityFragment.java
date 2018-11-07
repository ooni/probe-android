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
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.markwon.Markwon;

public class WebConnectivityFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
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
			Markwon.setMarkdown(desc, getString(R.string.TestResults_Details_Websites_LikelyBlocked_Content_Paragraph, measurement.url.url, getString(testKeys.getWebsiteBlocking())));
		} else {
			Markwon.setMarkdown(desc, getString(R.string.TestResults_Details_Websites_Reachable_Content_Paragraph, measurement.url.url));
		}
		return v;
	}
}
