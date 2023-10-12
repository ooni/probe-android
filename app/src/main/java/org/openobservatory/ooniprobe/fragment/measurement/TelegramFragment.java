package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementTelegramBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

public class TelegramFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static TelegramFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		TelegramFragment fragment = new TelegramFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		Measurement measurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert measurement != null;
		FragmentMeasurementTelegramBinding binding = FragmentMeasurementTelegramBinding.inflate(inflater,container,false);
		binding.desc.setText(measurement.is_anomaly ? R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Content_Paragraph : R.string.TestResults_Details_InstantMessaging_Telegram_Reachable_Content_Paragraph);
		binding.application.setText(measurement.getTestKeys().getTelegramEndpointStatus());
		if (Boolean.TRUE.equals(measurement.getTestKeys().telegram_http_blocking) || Boolean.TRUE.equals(measurement.getTestKeys().telegram_tcp_blocking))
			binding.application.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		binding.webApp.setText(measurement.getTestKeys().getTelegramWebStatus());
		if (TestKeys.BLOCKED.equals(measurement.getTestKeys().telegram_web_status))
			binding.webApp.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_yellow9));
		return binding.getRoot();
	}
}
