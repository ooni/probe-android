package org.openobservatory.ooniprobe.fragment.onboarding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.openobservatory.ooniprobe.databinding.FragmentOnboardingDialogWarningBinding;

public class OnboardingDialogWarningFragment extends DialogFragment {
	private static final String QUESTION_RES_ID = "questionResId";

	public static OnboardingDialogWarningFragment newInstance(int questionResId) {
		Bundle args = new Bundle();
		args.putInt(QUESTION_RES_ID, questionResId);
		OnboardingDialogWarningFragment fragment = new OnboardingDialogWarningFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		WindowManager.LayoutParams windowParams = getDialog().getWindow().getAttributes();
		windowParams.dimAmount = 0.90f;
		windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		getDialog().getWindow().setAttributes(windowParams);
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		assert getArguments() != null;
		FragmentOnboardingDialogWarningBinding binding = FragmentOnboardingDialogWarningBinding.inflate(inflater, container, false);
		binding.question.setText(getArguments().getInt(QUESTION_RES_ID));

		binding.positive.setOnClickListener(v -> positiveClick());
		binding.negative.setOnClickListener(v -> negativeClick());

		return binding.getRoot();
	}

	void positiveClick() {
		dismiss();
	}

	void negativeClick() {
		dismiss();
		((OnboardingWarningInterface) getParentFragment()).onWarningResult(getArguments().getInt(QUESTION_RES_ID));
	}

	public interface OnboardingWarningInterface {
		void onWarningResult(int questionResId);
	}
}
