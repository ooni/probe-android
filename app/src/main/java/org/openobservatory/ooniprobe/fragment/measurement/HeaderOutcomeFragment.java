package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementHeaderOutcomeBinding;

public class HeaderOutcomeFragment extends Fragment {
	public static final String ICON_RES = "iconRes";
	private static final String DESC = "desc";

	public static HeaderOutcomeFragment newInstance(Integer iconRes, String desc) {
		Bundle args = new Bundle();
		if (iconRes != null)
			args.putInt(ICON_RES, iconRes);
		args.putString(DESC, desc);
		HeaderOutcomeFragment fragment = new HeaderOutcomeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		assert getArguments() != null;
		FragmentMeasurementHeaderOutcomeBinding binding = FragmentMeasurementHeaderOutcomeBinding.inflate(inflater,container,false);
		binding.outcome.setText(Html.fromHtml(getArguments().getString(DESC)));
		if (getArguments().containsKey(ICON_RES))
			binding.outcome.setCompoundDrawablesRelativeWithIntrinsicBounds(0, getArguments().getInt(ICON_RES), 0, 0);
		return binding.getRoot();
	}
}
