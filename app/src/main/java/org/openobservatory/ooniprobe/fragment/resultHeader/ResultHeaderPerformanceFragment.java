package org.openobservatory.ooniprobe.fragment.resultHeader;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultHeaderPerformanceFragment extends Fragment {
	public static final String RESULT = "result";
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

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_head_performance, container, false);
		ButterKnife.bind(this, v);
		Result result = (Result) getArguments().getSerializable(RESULT);
		assert result != null;
		Measurement dashM = result.getMeasurement(Dash.NAME);
		Measurement ndtM = result.getMeasurement(Ndt.NAME);
		if (dashM != null) {
			TestKeys dashTK = dashM.getTestKeys();
			video.setText(dashTK.getVideoQuality(getActivity(), false));
		}
		if (ndtM != null) {
			TestKeys ndtTK = ndtM.getTestKeys();
			upload.setText(ndtTK.getUpload(getActivity()));
			uploadUnit.setText(ndtTK.getUploadUnit(getActivity()));
			download.setText(ndtTK.getDownload(getActivity()));
			downloadUnit.setText(ndtTK.getDownloadUnit(getActivity()));
			ping.setText(ndtTK.getPing(getActivity()));
		}
		return v;
	}
}
