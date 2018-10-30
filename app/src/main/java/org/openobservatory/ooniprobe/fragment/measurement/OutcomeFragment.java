package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OutcomeFragment extends Fragment {
	public static final String DESC = "desc";
	private static final String SUCCESS = "success";
	@BindView(R.id.outcome) TextView outcome;

	public static OutcomeFragment newInstance(Boolean success, String desc) {
		Bundle args = new Bundle();
		if (success != null)
			args.putBoolean(SUCCESS, success);
		args.putString(DESC, desc);
		OutcomeFragment fragment = new OutcomeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_outcome, container, false);
		ButterKnife.bind(this, v);
		outcome.setText(Html.fromHtml(getArguments().getString(DESC)));
		if (getArguments().containsKey(SUCCESS))
			outcome.setCompoundDrawablesRelativeWithIntrinsicBounds(0, getArguments().getBoolean(SUCCESS) ? R.drawable.tick_white_48dp : R.drawable.exclamation_white_48dp, 0, 0);
		return v;
	}
}
