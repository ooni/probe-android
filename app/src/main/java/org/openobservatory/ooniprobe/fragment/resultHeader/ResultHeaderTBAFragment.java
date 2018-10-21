package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Result;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderTBAFragment extends Fragment {
	public static final String RESULT = "result";
	@BindView(R.id.tested) TextView tested;
	@BindView(R.id.blocked) TextView blocked;
	@BindView(R.id.available) TextView available;
	@BindView(R.id.testedTag) TextView testedTag;
	@BindView(R.id.blockedTag) TextView blockedTag;
	@BindView(R.id.availableTag) TextView availableTag;

	public static ResultHeaderTBAFragment newInstance(Result result) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		ResultHeaderTBAFragment fragment = new ResultHeaderTBAFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_head_tba, container, false);
		ButterKnife.bind(this, v);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		long testedCount = result.countTotalMeasurements();
		long blockedCount = result.countAnomalousMeasurements();
		long availableCount = result.countOkMeasurements();
		tested.setText(getString(R.string.d, testedCount));
		blocked.setText(getString(R.string.d, blockedCount));
		available.setText(getString(R.string.d, availableCount));
		testedTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Tested, (int) testedCount));
		blockedTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Blocked, (int) blockedCount));
		availableTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Reachable, (int) availableCount));
		return v;
	}
}
