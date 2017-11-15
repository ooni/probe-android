package org.openobservatory.ooniprobe.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage4Fragment extends Fragment {

    private InformedConsentActivity mActivity;
    private AppCompatButton trueButton;
    private AppCompatButton falseButton;
    private TextView questionNumber;
    private TextView questionText;
    private LottieAnimationView animationView;

    public static IConsentPage4Fragment create() {
        IConsentPage4Fragment atf = new IConsentPage4Fragment();
        Bundle args = new Bundle();
        atf.setArguments(args);
        return atf;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (InformedConsentActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onViewSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ic_page_4, container, false);
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

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                mActivity.getWizard().navigateNext();
                            }
                            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                mActivity.getWizard().navigatePrevious();
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
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
        animationView.setBackgroundColor(answer? getResources().getColor(R.color.color_teal5): getResources().getColor(R.color.color_red6));
        animationView.setAnimation(answer? "anim/checkMark.json" : "anim/crossMark.json");
        animationView.setVisibility(View.VISIBLE);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                next(answer);
                if (answer)
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
        if (mActivity.QUESTION_NUMBER == 1) {
            if (!answer){
                FragmentManager fm = getFragmentManager();
                IConsentWrongAnswerFragment dFragment = new IConsentWrongAnswerFragment();
                dFragment.mActivity = mActivity;
                dFragment.show(fm, "Dialog Fragment");
            }
            else {
                mActivity.QUESTION_NUMBER = 2;
                loadView();
            }
        }
        else if (mActivity.QUESTION_NUMBER == 2) {
            if (!answer){
                FragmentManager fm = getFragmentManager();
                IConsentWrongAnswerFragment dFragment = new IConsentWrongAnswerFragment();
                dFragment.mActivity = mActivity;
                dFragment.show(fm, "Dialog Fragment");
            }
            else {
                mActivity.getWizard().navigateNext();
            }
        }
    }

}
