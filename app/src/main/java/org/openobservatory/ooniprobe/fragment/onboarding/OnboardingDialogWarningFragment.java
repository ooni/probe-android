package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingDialogWarningFragment extends DialogFragment {
	private static final String QUESTION_RES_ID = "questionResId";
	@BindView(R.id.title) @Nullable TextView title;
	@BindView(R.id.question) TextView question;
	@BindView(R.id.dialog) LinearLayout dialog;
	@BindView(R.id.animation) LottieAnimationView animation;

	public static OnboardingDialogWarningFragment newInstance(int questionResId) {
		Bundle args = new Bundle();
		args.putInt(QUESTION_RES_ID, questionResId);
		OnboardingDialogWarningFragment fragment = new OnboardingDialogWarningFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
