package org.openobservatory.ooniprobe.fragment.onboarding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentOnboardingDialogPopquizBinding;

public class OnboardingDialogPopquizFragment extends DialogFragment {
	private static final String TITLE_RES_ID = "titleResId";
	private static final String QUESTION_RES_ID = "questionResId";
	private FragmentOnboardingDialogPopquizBinding binding;

	public static OnboardingDialogPopquizFragment newInstance(int titleResId, int questionResId) {
		Bundle args = new Bundle();
		args.putInt(TITLE_RES_ID, titleResId);
		args.putInt(QUESTION_RES_ID, questionResId);
		OnboardingDialogPopquizFragment fragment = new OnboardingDialogPopquizFragment();
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
		binding = FragmentOnboardingDialogPopquizBinding.inflate(inflater, container, false);
		if (binding.title != null)
			binding.title.setText(getArguments().getInt(TITLE_RES_ID));
		binding.question.setText(getArguments().getInt(QUESTION_RES_ID));

		binding.positive.setOnClickListener(v -> positiveClick());
		binding.negative.setOnClickListener(v -> negativeClick());

		return binding.getRoot();
	}

	void positiveClick() {
		binding.animation.setBackgroundResource(R.drawable.dialog_green);
		binding.animation.setAnimation("anim/checkMark.json");
		binding.animation.setVisibility(View.VISIBLE);
		binding.dialog.setVisibility(View.INVISIBLE);
		binding.animation.addAnimatorListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				dismiss();
				((OnboardingPopquizInterface) getParentFragment()).onPopquizResult(getArguments().getInt(QUESTION_RES_ID), true);
			}
		});
		binding.animation.playAnimation();
	}

	void negativeClick() {
		binding.animation.setBackgroundResource(R.drawable.dialog_red);
		binding.animation.setAnimation("anim/crossMark.json");
		binding.animation.setVisibility(View.VISIBLE);
		binding.dialog.setVisibility(View.INVISIBLE);
		binding.animation.addAnimatorListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				dismiss();
				((OnboardingPopquizInterface) getParentFragment()).onPopquizResult(getArguments().getInt(QUESTION_RES_ID), false);
			}
		});
		binding.animation.playAnimation();
	}

	public interface OnboardingPopquizInterface {
		void onPopquizResult(int questionResId, boolean positive);
	}
}
