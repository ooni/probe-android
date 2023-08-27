package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentResultHeadTbaBinding;
import org.openobservatory.ooniprobe.model.database.Result;

public class ResultHeaderTBAFragment extends Fragment {
	private static final String RESULT = "result";

	public static ResultHeaderTBAFragment newInstance(Result result) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		ResultHeaderTBAFragment fragment = new ResultHeaderTBAFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		FragmentResultHeadTbaBinding binding = FragmentResultHeadTbaBinding.inflate(inflater, container, false);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		long testedCount = result.countTotalMeasurements();
		long blockedCount = result.countAnomalousMeasurements();
		long availableCount = result.countOkMeasurements();
		binding.tested.setText(getString(R.string.d, testedCount));
		binding.blocked.setText(getString(R.string.d, blockedCount));
		binding.available.setText(getString(R.string.d, availableCount));
		binding.testedTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Tested, (int) testedCount));
		binding.blockedTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Blocked, (int) blockedCount));
		binding.availableTag.setText(getResources().getQuantityText(R.plurals.TestResults_Summary_Websites_Hero_Reachable, (int) availableCount));
		return binding.getRoot();
	}
}
