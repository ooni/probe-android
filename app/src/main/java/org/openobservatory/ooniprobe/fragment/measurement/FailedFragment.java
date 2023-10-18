package org.openobservatory.ooniprobe.fragment.measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.databinding.FragmentMeasurementFailedBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Collections;

import javax.inject.Inject;

public class FailedFragment extends Fragment {
	private static final String MEASUREMENT = "measurement";

	@Inject
	PreferenceManager preferenceManager;
	private FragmentMeasurementFailedBinding binding;

	public static FailedFragment newInstance(Measurement measurement) {
		Bundle args = new Bundle();
		args.putSerializable(MEASUREMENT, measurement);
		FailedFragment fragment = new FailedFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentMeasurementFailedBinding.inflate(inflater,container,false);
		binding.tryAgain.setOnClickListener(this::tryAgainClick);
		return binding.getRoot();
	}

	void tryAgainClick(View view) {
		assert getArguments() != null;
		Measurement failedMeasurement = (Measurement) getArguments().getSerializable(MEASUREMENT);
		assert failedMeasurement != null;
		failedMeasurement.result.load();
		AbstractTest abstractTest = failedMeasurement.getTest();
		abstractTest.setIsRerun(true);
		if (failedMeasurement.url != null)
			abstractTest.setInputs(Collections.singletonList(failedMeasurement.url.url));
		AbstractSuite testSuite = failedMeasurement.result.getTestSuite();
		testSuite.setTestList(abstractTest);
		testSuite.setResult(failedMeasurement.result);

		RunningActivity.runAsForegroundService((AbstractActivity) getActivity(),
				testSuite.asArray(),
				() -> {
					try {
						failedMeasurement.setReRun(getContext());
						getActivity().finish();
					} catch (NullPointerException exception){
						exception.printStackTrace();
						ThirdPartyServices.logException(exception);
					}
				},preferenceManager);
	}
}
