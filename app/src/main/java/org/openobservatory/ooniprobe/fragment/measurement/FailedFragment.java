package org.openobservatory.ooniprobe.fragment.measurement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FailedFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	public static FailedFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		FailedFragment fragment = new FailedFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_measurement_failed, container, false);
		ButterKnife.bind(this, v);
		return v;
	}

	@OnClick(R.id.tryAgain) void tryAgainClick() {
		assert getArguments() != null;
		Measurement failedMeasurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert failedMeasurement != null;
		failedMeasurement.result.load();
		AbstractTest abstractTest = failedMeasurement.getTest();
		if (failedMeasurement.url != null)
			abstractTest.setInputs(Collections.singletonList(failedMeasurement.url.url));
		AbstractSuite testSuite = failedMeasurement.result.getTestSuite();
		testSuite.setTestList(abstractTest);
		testSuite.setResult(failedMeasurement.result);
		failedMeasurement.is_rerun = true;
		failedMeasurement.save();
		Intent intent = RunningActivity.newIntent((AbstractActivity) getActivity(), testSuite);
		if (intent != null) {
			startActivity(intent);
			getActivity().finish();
		}
	}
}
