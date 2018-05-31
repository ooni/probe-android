package org.openobservatory.ooniprobe.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentQuizFragment extends DialogFragment {

    public InformedConsentActivity mActivity;
    private AppCompatButton trueButton;
    private AppCompatButton falseButton;
    private TextView questionNumber;
    private TextView questionText;
    private LottieAnimationView animationView;

    //make background darker, from https://stackoverflow.com/questions/13822842/
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

        View v = inflater.inflate(R.layout.fragment_ic_quiz, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        questionNumber = v.findViewById(R.id.question_number);
        questionText = v.findViewById(R.id.question_text);
        animationView = v.findViewById(R.id.animationView);
        animationView.setVisibility(View.GONE);
        animationView.setImageAssetsFolder("anim/");

        loadView();

        trueButton = v.findViewById(R.id.trueButton);
        trueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                answer(true);
            }
        });
        falseButton = v.findViewById(R.id.falseButton);
        falseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                answer(false);
            }
        });
        return v;
    }

    public void loadView(){
        if (mActivity.QUESTION_NUMBER == 1) {
            questionNumber.setText(mActivity.getString(R.string.Onboarding_PopQuiz_1_Title));
            questionText.setText(mActivity.getString(R.string.Onboarding_PopQuiz_1_Question));
        }
        else if (mActivity.QUESTION_NUMBER == 2) {
            questionNumber.setText(mActivity.getString(R.string.Onboarding_PopQuiz_2_Title));
            questionText.setText(mActivity.getString(R.string.Onboarding_PopQuiz_2_Question));
        }
    }

    private void answer(final Boolean answer){
        animationView.setBackgroundDrawable(answer? getResources().getDrawable(R.drawable.dialog_green) : getResources().getDrawable(R.drawable.dialog_red));
        animationView.setAnimation(answer? "anim/checkMark.json" : "anim/crossMark.json");
        animationView.setVisibility(View.VISIBLE);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                next(answer);
                removeAnim();
                animationView.removeAnimatorListener(this);
            }
        });
        animationView.playAnimation();
    }

    public void removeAnim(){
        animationView.setVisibility(View.GONE);
        animationView.setProgress(0);
    }

    private void next(final Boolean answer){
        if (!answer){
            showWrong();
        }
        else if (mActivity.QUESTION_NUMBER == 1) {
            mActivity.QUESTION_NUMBER = 2;
            loadView();
        }
        else if (mActivity.QUESTION_NUMBER == 2) {
            mActivity.QUESTION_NUMBER = 3;
            dismiss();
            mActivity.hideNextButton();
            mActivity.setNextPageSwipeLock(false);
            mActivity.getPager().setCurrentItem(4);
        }
    }

    private void showWrong(){
        dismiss();
        FragmentManager fm = getFragmentManager();
        IConsentWrongAnswerFragment dFragment = new IConsentWrongAnswerFragment();
        dFragment.mActivity = mActivity;
        dFragment.quizFragment = this;
        dFragment.show(fm, "wrong_answer");
    }
}
