package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.AppIntroFragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage3Fragment extends Fragment {

    private InformedConsentActivity mActivity;
    private AppCompatButton nextButton;

    public static IConsentPage3Fragment create() {
        IConsentPage3Fragment atf = new IConsentPage3Fragment();
        Bundle args = new Bundle();
        atf.setArguments(args);
        return atf;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ic_page_3, container, false);
        nextButton = (AppCompatButton) v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mActivity.QUESTION_NUMBER < 3) {
                    mActivity.loadQuizFragment();
                    /*
                    FragmentManager fm = getFragmentManager();
                    IConsentQuizFragment dFragment = new IConsentQuizFragment();
                    dFragment.mActivity = mActivity;
                    dFragment.show(fm, "quiz");
                    */
                }
                //else
                    //mActivity.getWizard().navigateNext();
            }
        });
        /*
        learn_moreButton = (AppCompatButton) v.findViewById(R.id.learn_more_button);
        learn_moreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/about/risks/"));
                startActivity(browserIntent);
            }
        });*/

        return v;
    }

    public void hideNextButton(){
        nextButton.setVisibility(View.GONE);
    }

}
