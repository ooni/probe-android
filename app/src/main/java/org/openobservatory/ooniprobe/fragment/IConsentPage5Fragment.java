package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SwitchCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage5Fragment extends Fragment {

    private InformedConsentActivity mActivity;
    private AppCompatButton nextButton;
    private AppCompatButton changeButton;

    public static IConsentPage5Fragment create() {
        IConsentPage5Fragment atf = new IConsentPage5Fragment();
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
        View v = inflater.inflate(R.layout.fragment_ic_page_5, container, false);

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
                            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                mActivity.getWizard().navigatePrevious();
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

        nextButton = v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //next();
            }
        });

        changeButton= v.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //next();
            }
        });

        return v;
    }

    private void next() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
        editor.putBoolean("include_ip", false);
        editor.putBoolean("include_asn", true);
        editor.putBoolean("include_country", true);
        editor.putBoolean("upload_results", true);
        editor.apply();
        mActivity.getWizard().navigateNext();
    }
}
