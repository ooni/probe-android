package org.openobservatory.ooniprobe.fragment.resultHeader;

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

public class ResultHeaderMiddleboxFragment extends Fragment {
	public static final String ANOMALY = "anomaly";
	@BindView(R.id.text) TextView text;

	public static ResultHeaderMiddleboxFragment newInstance(boolean anomaly) {
		Bundle args = new Bundle();
		args.putBoolean(ANOMALY, anomaly);
		ResultHeaderMiddleboxFragment fragment = new ResultHeaderMiddleboxFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		View v = inflater.inflate(R.layout.fragment_result_head_middlebox, container, false);
		ButterKnife.bind(this, v);
		text.setText(Html.fromHtml(v.getContext().getString(R.string.normalBold, getString(R.string.Test_Middleboxes_Fullname), getString(getArguments().getBoolean(ANOMALY) ? R.string.TestResults_Summary_Middleboxes_Hero_Found : R.string.TestResults_Summary_Middleboxes_Hero_NotFound))));
		return v;
	}
}
