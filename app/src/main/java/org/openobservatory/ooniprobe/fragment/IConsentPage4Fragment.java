package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.AppIntroFragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage4Fragment extends Fragment {

    private InformedConsentActivity mActivity;


    public static IConsentPage4Fragment create() {
        IConsentPage4Fragment atf = new IConsentPage4Fragment();
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
        View v = inflater.inflate(R.layout.fragment_ic_page_4, container, false);
        return v;
    }

}
