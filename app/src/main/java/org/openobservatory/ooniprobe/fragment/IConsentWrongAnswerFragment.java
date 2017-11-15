package org.openobservatory.ooniprobe.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentWrongAnswerFragment extends DialogFragment {
    public InformedConsentActivity mActivity;
    private AppCompatButton continueButton;
    private AppCompatButton backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ic_wrong_answer, container,
                false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IConsentPage4Fragment current = (IConsentPage4Fragment)mActivity.getWizard().getCurrent();
                current.removeAnim();
            }
        }, 100);

        continueButton = v.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mActivity.QUESTION_NUMBER == 1) {
                    mActivity.QUESTION_NUMBER = 2;
                    IConsentPage4Fragment current = (IConsentPage4Fragment)mActivity.getWizard().getCurrent();
                    current.removeAnim();
                    //current.loadView();
                    dismiss();
                }
                else {
                    mActivity.getWizard().navigateNext();
                    dismiss();
                }
            }
        });

        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mActivity.getWizard().navigatePrevious();
                dismiss();
            }
        });

        return v;
    }

}
