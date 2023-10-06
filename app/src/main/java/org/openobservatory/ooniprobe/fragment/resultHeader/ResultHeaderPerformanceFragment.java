package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentResultHeadPerformanceBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

public class ResultHeaderPerformanceFragment extends Fragment {
	private static final String RESULT = "result";
	public static final float ALPHA_DIS = 0.3f;
	public static final int ALPHA_ENA = 1;

	public static ResultHeaderPerformanceFragment newInstance(Result result) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		ResultHeaderPerformanceFragment fragment = new ResultHeaderPerformanceFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		FragmentResultHeadPerformanceBinding binding = FragmentResultHeadPerformanceBinding.inflate(inflater, container, false);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		Measurement dashM = result.getMeasurement(Dash.NAME);
		Measurement ndtM = result.getMeasurement(Ndt.NAME);
		binding.video.setText(dashM == null ? R.string.TestResults_NotAvailable : dashM.getTestKeys().getVideoQuality(false));
		binding.upload.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getUpload(getActivity()));
		binding.uploadUnit.setText(ndtM == null ? R.string.TestResults_NotAvailable : ndtM.getTestKeys().getUploadUnit());
		binding.download.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getDownload(getActivity()));
		binding.downloadUnit.setText(ndtM == null ? R.string.TestResults_NotAvailable : ndtM.getTestKeys().getDownloadUnit());
		binding.ping.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getPing(getActivity()));
		binding.videoLabel.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.downloadLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.uploadLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.pingLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.video.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.download.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.upload.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.ping.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.videoUnit.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.downloadUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.uploadUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		binding.pingUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		return binding.getRoot();
	}
}
