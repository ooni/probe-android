package org.openobservatory.ooniprobe.fragment.onboarding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.openobservatory.ooniprobe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingDialogWarningFragment extends DialogFragment {
	private static final String QUESTION_RES_ID = "questionResId";
	@BindView(R.id.title) @Nullable TextView title;
	@BindView(R.id.question) TextView question;
	@BindView(R.id.dialog) LinearLayout dialog;

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
		View v = inflater.inflate(R.layout.fragment_onboarding_dialog_warning, container, false);
		ButterKnife.bind(this, v);
		question.setText(getArguments().getInt(QUESTION_RES_ID));
		return v;
	}

	@OnClick(R.id.positive) void positiveClick() {
		dismiss();
	}

	@OnClick(R.id.negative) void negativeClick() {
		dismiss();
		((OnboardingWarningInterface) getParentFragment()).onWarningResult(getArguments().getInt(QUESTION_RES_ID));
	}

	public interface OnboardingWarningInterface {
		void onWarningResult(int questionResId);
	}
}
