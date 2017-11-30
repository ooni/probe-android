package org.openobservatory.ooniprobe.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        questionNumber.setText(mActivity.getString(R.string.question) + " " + mActivity.QUESTION_NUMBER + "/2");
        if (mActivity.QUESTION_NUMBER == 1)
            questionText.setText(mActivity.getString(R.string.question_1));
        else if (mActivity.QUESTION_NUMBER == 2)
            questionText.setText(mActivity.getString(R.string.question_2));
    }

    private void answer(final Boolean answer){
        animationView.setBackgroundDrawable(answer? getResources().getDrawable(R.drawable.dialog_green) : getResources().getDrawable(R.drawable.dialog_red));
        animationView.setAnimation(answer? "anim/checkMark.json" : "anim/crossMark.json");
        animationView.setVisibility(View.VISIBLE);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                next(answer);
                //if (answer)
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
        }
    }

    private void showWrong(){
        dismiss();
        FragmentManager fm = getFragmentManager();
        IConsentWrongAnswerFragment dFragment = new IConsentWrongAnswerFragment();
        dFragment.mActivity = mActivity;
        dFragment.quizFragment = this;
        //questo da(va) problemi nel dismiss
        dFragment.show(fm, "wrong_answer");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        //appear on centre of other fragment
        //ft.add(dFragment, "wrong_answer");

        //fade on top of the other fragment
        //ft.replace(R.id.fragmentHolder, dFragment, null);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //ft.commit();

        //ft.addToBackStack(null);
/*
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragmentManager fm = getFragmentManager();
                IConsentWrongAnswerFragment dFragment = new IConsentWrongAnswerFragment();
                dFragment.mActivity = mActivity;
                //dFragment.show(ft, "wrong_answer");
                ft.add(dFragment, "wrong_answer");
                ft.commit();


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("quiz");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
                newFragment.show(ft, "dialog");
                */

    }
}
