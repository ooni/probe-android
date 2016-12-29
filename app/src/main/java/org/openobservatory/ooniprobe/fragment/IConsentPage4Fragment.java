package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SwitchCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage4Fragment extends Fragment {


    private InformedConsentActivity mActivity;
    private SwitchCompat mCkIncludeIP;
    private SwitchCompat mCkIncludeAsn;
    private SwitchCompat mCkIncludeCountry;
    private SwitchCompat mCkUploadResults;

    public static IConsentPage4Fragment create() {
        IConsentPage4Fragment atf = new IConsentPage4Fragment();
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
        View v = inflater.inflate(R.layout.fragment_ic_page_4, container, false);
        mCkIncludeIP = (SwitchCompat) v.findViewById(R.id.ck_include_ip);
        mCkIncludeAsn = (SwitchCompat) v.findViewById(R.id.ck_include_asn);
        mCkIncludeCountry = (SwitchCompat) v.findViewById(R.id.ck_include_country);
        mCkUploadResults = (SwitchCompat) v.findViewById(R.id.ck_upload_results);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_informed_consent, menu);
        menu.findItem(R.id.menu_next).setTitle(R.string.configure);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mActivity.getWizard().navigatePrevious();
                break;
            case R.id.menu_next:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                editor.putBoolean("include_ip", mCkIncludeIP.isChecked());
                editor.putBoolean("include_asn", mCkIncludeAsn.isChecked());
                editor.putBoolean("include_country", mCkIncludeCountry.isChecked());
                editor.putBoolean("upload_results", mCkUploadResults.isChecked());
                editor.apply();
                mActivity.getWizard().navigateNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}