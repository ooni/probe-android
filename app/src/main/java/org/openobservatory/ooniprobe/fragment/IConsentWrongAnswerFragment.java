package org.openobservatory.ooniprobe.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentWrongAnswerFragment extends DialogFragment {
    public InformedConsentActivity mActivity;
    private AppCompatButton continueButton;
    private AppCompatButton backButton;
    public IConsentQuizFragment quizFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ic_wrong_answer, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        continueButton = v.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mActivity.QUESTION_NUMBER == 1) {
                    mActivity.QUESTION_NUMBER = 2;
                    //quizFragment.loadView();
                    dismiss();
                    mActivity.loadQuizFragment();
                }
                else {
                    mActivity.QUESTION_NUMBER = 3;
                    //quizFragment.dismiss();
                    dismiss();
                    //mActivity.getWizard().navigateNext();
                }
            }
        });

        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //quizFragment.dismiss();
                dismiss();
            }
        });

        return v;
    }

}
