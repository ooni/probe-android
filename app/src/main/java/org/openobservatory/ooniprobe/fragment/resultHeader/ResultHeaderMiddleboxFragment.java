package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentResultHeadMiddleboxBinding;

@Deprecated
public class ResultHeaderMiddleboxFragment extends Fragment {
	private static final String ANOMALY = "anomaly";

	public static ResultHeaderMiddleboxFragment newInstance(boolean anomaly) {
		Bundle args = new Bundle();
		args.putBoolean(ANOMALY, anomaly);
		ResultHeaderMiddleboxFragment fragment = new ResultHeaderMiddleboxFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		FragmentResultHeadMiddleboxBinding binding = FragmentResultHeadMiddleboxBinding.inflate(inflater, container, false);
		binding.text.setText(
				Html.fromHtml(
						binding.getRoot().getContext().getString(
								R.string.normalBold,
								getString(R.string.Test_Middleboxes_Fullname),
								getString(getArguments().getBoolean(ANOMALY) ? R.string.TestResults_Summary_Middleboxes_Hero_Found : R.string.TestResults_Summary_Middleboxes_Hero_NotFound)
						)
				)
		);
		return binding.getRoot();
	}
}
