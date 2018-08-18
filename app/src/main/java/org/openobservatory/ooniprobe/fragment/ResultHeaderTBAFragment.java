package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ResultHeaderTBAFragment extends Fragment {
	public static final String LABEL = "label";
	public static final String RESULT = "result";
	@BindView(R.id.tested) TextView tested;
	@BindView(R.id.blocked) TextView blocked;
	@BindView(R.id.available) TextView available;
	@BindViews({R.id.testedLabel, R.id.blockedLabel, R.id.availableLabel}) List<TextView> label;

	public static ResultHeaderTBAFragment newInstance(Result result, String label) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		args.putString(LABEL, label);
		ResultHeaderTBAFragment fragment = new ResultHeaderTBAFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_head_tba, container, false);
		ButterKnife.bind(this, v);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		tested.setText(getString(R.string.d, result.countMeasurement(null, false)));
		blocked.setText(getString(R.string.d, result.countMeasurement(true, false)));
		available.setText(getString(R.string.d, result.countMeasurement(false, false)));
		for (TextView tv : label)
			tv.setText(getArguments().getString(LABEL));
		return v;
	}
}
