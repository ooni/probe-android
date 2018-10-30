package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HttpInvalidRequestLineFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.desc1) TextView desc1;
	@BindView(R.id.desc2) TextView desc2;
	@BindView(R.id.sent) LinearLayout sent;
	@BindView(R.id.received) LinearLayout received;

	public static HttpInvalidRequestLineFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		HttpInvalidRequestLineFragment fragment = new HttpInvalidRequestLineFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_httpinvalidrequestline, container, false);
		ButterKnife.bind(this, v);
		if (measurement.is_anomaly) {
			desc1.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Content_Paragraph);
			desc2.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Content_Paragraph);
		} else {
			desc1.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_NotFound_Content_Paragraph);
		}
		TestKeys testKeys = measurement.getTestKeys();
		if (testKeys != null) {
			for (String value : testKeys.sent) {
				TextView view = (TextView) inflater.inflate(R.layout.item_sent_received, sent, false);
				view.setText(value);
				sent.addView(view);
			}
			for (String value : testKeys.received) {
				TextView view = (TextView) inflater.inflate(R.layout.item_sent_received, received, false);
				view.setText(value);
				received.addView(view);
			}
		}
		return v;
	}
}
