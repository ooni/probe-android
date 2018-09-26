package org.openobservatory.ooniprobe.fragment.consent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentWrongAnswerFragment extends DialogFragment {
	public InformedConsentActivity mActivity;
	public IConsentQuizFragment quizFragment;
	private AppCompatButton continueButton;
	private AppCompatButton backButton;

	@Override
	public void onStart() {
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams windowParams = window.getAttributes();
		windowParams.dimAmount = 0.90f;
		windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(windowParams);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_ic_wrong_answer, container,
				false);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		continueButton = v.findViewById(R.id.continueButton);
		continueButton.setOnClickListener(v1 -> {
			if (mActivity.QUESTION_NUMBER == 1) {
				mActivity.QUESTION_NUMBER = 2;
				dismiss();
				mActivity.loadQuizFragment();
			} else {
				mActivity.QUESTION_NUMBER = 3;
				dismiss();
				mActivity.hideNextButton();
				mActivity.setNextPageSwipeLock(false);
				mActivity.getPager().setCurrentItem(4);
			}
		});
		backButton = v.findViewById(R.id.backButton);
		backButton.setOnClickListener(v12 -> dismiss());
		TextView actually = v.findViewById(R.id.actually);
		TextView wrongAnswer = v.findViewById(R.id.wrongAsnwer);
		if (mActivity.QUESTION_NUMBER == 1) {
			actually.setText(mActivity.getString(R.string.Onboarding_PopQuiz_1_Wrong_Title));
			wrongAnswer.setText(mActivity.getString(R.string.Onboarding_PopQuiz_1_Wrong_Paragraph));
		} else if (mActivity.QUESTION_NUMBER == 2) {
			actually.setText(mActivity.getString(R.string.Onboarding_PopQuiz_2_Wrong_Title));
			wrongAnswer.setText(mActivity.getString(R.string.Onboarding_PopQuiz_2_Wrong_Paragraph));
		}
		return v;
	}
}
