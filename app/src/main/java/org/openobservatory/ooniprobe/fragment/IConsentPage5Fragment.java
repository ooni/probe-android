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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        nextButton = v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next();
            }
        });

        changeButton= v.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next();
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
