package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderPerformanceFragment extends Fragment {
	private static final String RESULT = "result";
	public static final float ALPHA_DIS = 0.3f;
	public static final int ALPHA_ENA = 1;
	@BindView(R.id.video) TextView video;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.ping) TextView ping;
	@BindView(R.id.videoLabel) TextView videoLabel;
	@BindView(R.id.downloadLabel) TextView downloadLabel;
	@BindView(R.id.uploadLabel) TextView uploadLabel;
	@BindView(R.id.pingLabel) TextView pingLabel;
	@BindView(R.id.videoUnit) TextView videoUnit;
	@BindView(R.id.downloadUnit) TextView downloadUnit;
	@BindView(R.id.uploadUnit) TextView uploadUnit;
	@BindView(R.id.pingUnit) TextView pingUnit;

	public static ResultHeaderPerformanceFragment newInstance(Result result) {
		Bundle args = new Bundle();
		args.putSerializable(RESULT, result);
		ResultHeaderPerformanceFragment fragment = new ResultHeaderPerformanceFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		assert getArguments() != null;
		View v = inflater.inflate(R.layout.fragment_result_head_performance, container, false);
		ButterKnife.bind(this, v);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		Measurement dashM = result.getMeasurement(Dash.NAME);
		Measurement ndtM = result.getMeasurement(Ndt.NAME);
		video.setText(dashM == null ? R.string.TestResults_NotAvailable : dashM.getTestKeys().getVideoQuality(false));
		upload.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getUpload(getActivity()));
		uploadUnit.setText(ndtM == null ? R.string.TestResults_NotAvailable : ndtM.getTestKeys().getUploadUnit());
		download.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getDownload(getActivity()));
		downloadUnit.setText(ndtM == null ? R.string.TestResults_NotAvailable : ndtM.getTestKeys().getDownloadUnit());
		ping.setText(ndtM == null ? getString(R.string.TestResults_NotAvailable) : ndtM.getTestKeys().getPing(getActivity()));
		videoLabel.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		downloadLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		uploadLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		pingLabel.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		video.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		download.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		upload.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		ping.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		videoUnit.setAlpha(dashM == null ? ALPHA_DIS : ALPHA_ENA);
		downloadUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		uploadUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		pingUnit.setAlpha(ndtM == null ? ALPHA_DIS : ALPHA_ENA);
		return v;
	}
}
