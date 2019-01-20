package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderPerformanceFragment extends Fragment {
	private static final String RESULT = "result";
	@BindView(R.id.video) TextView video;
	@BindView(R.id.upload) TextView upload;
	@BindView(R.id.uploadUnit) TextView uploadUnit;
	@BindView(R.id.download) TextView download;
	@BindView(R.id.downloadUnit) TextView downloadUnit;
	@BindView(R.id.ping) TextView ping;

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
		if (dashM != null)
			video.setText(dashM.getTestKeys().getVideoQuality(false));
		if (ndtM != null) {
			upload.setText(ndtM.getTestKeys().getUpload(getActivity()));
			uploadUnit.setText(ndtM.getTestKeys().getUploadUnit());
			download.setText(ndtM.getTestKeys().getDownload(getActivity()));
			downloadUnit.setText(ndtM.getTestKeys().getDownloadUnit());
			ping.setText(ndtM.getTestKeys().getPing(getActivity()));
		}
		return v;
	}
}
