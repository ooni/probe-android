package org.openobservatory.ooniprobe.fragment.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.TestKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HttpInvalidRequestLineFragment extends Fragment {
	public static final String MEASUREMENT = "measurement";
	@BindView(R.id.title) TextView title;
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
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.exclamation_point, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow8));	title.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Hero_Title);
			desc1.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Content_Paragraph_1);
			desc2.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Content_Paragraph_2);
		} else {
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tick, 0, 0);
			title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_green7));
			title.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_NotFound_Hero_Title);
			desc1.setText(R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_NotFound_Content_Paragraph_1);
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
