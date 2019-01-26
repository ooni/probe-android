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

public class TelegramFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";
	@BindView(R.id.application) TextView application;
	@BindView(R.id.webApp) TextView webApp;
	@BindView(R.id.desc) TextView desc;

	public static TelegramFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		TelegramFragment fragment = new TelegramFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		View v = inflater.inflate(R.layout.fragment_measurement_telegram, container, false);
		ButterKnife.bind(this, v);
		desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_Telegram_Reachable_Content_Paragraph);
		application.setText(measurement.getTestKeys().getTelegramEndpointStatus());
		if (Boolean.TRUE.equals(measurement.getTestKeys().telegram_http_blocking) || Boolean.TRUE.equals(measurement.getTestKeys().telegram_tcp_blocking))
			application.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		webApp.setText(measurement.getTestKeys().getTelegramWebStatus(getActivity()));
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().telegram_web_status))
			webApp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		return v;
	}
}
