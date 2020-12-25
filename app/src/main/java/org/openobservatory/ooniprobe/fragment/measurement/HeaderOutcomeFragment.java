package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderOutcomeFragment extends Fragment {
	public static final String ICON_RES = "iconRes";
	private static final String DESC = "desc";
	@BindView(R.id.outcome) TextView outcome;

	public static HeaderOutcomeFragment newInstance(Integer iconRes, String desc) {
		Bundle args = new Bundle();
		if (iconRes != null)
			args.putInt(ICON_RES, iconRes);
		args.putString(DESC, desc);
		HeaderOutcomeFragment fragment = new HeaderOutcomeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		assert getArguments() != null;
		View v = inflater.inflate(R.layout.fragment_measurement_header_outcome, container, false);
		ButterKnife.bind(this, v);
		outcome.setText(Html.fromHtml(getArguments().getString(DESC)));
		if (getArguments().containsKey(ICON_RES))
			outcome.setCompoundDrawablesRelativeWithIntrinsicBounds(0, getArguments().getInt(ICON_RES), 0, 0);
		return v;
	}
}
