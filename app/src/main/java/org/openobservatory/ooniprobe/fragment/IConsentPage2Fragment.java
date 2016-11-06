package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

public class IConsentPage2Fragment extends Fragment {


    private InformedConsentActivity mActivity;

    public static IConsentPage2Fragment create() {
        IConsentPage2Fragment atf = new IConsentPage2Fragment();
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

        View v = inflater.inflate(R.layout.fragment_ic_page_2, container, false);
        WebView webview = (WebView)v.findViewById(R.id.wv);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("", getString(R.string.risks_text), "text/html", "UTF-8", "");


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_informed_consent, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mActivity.getWizard().navigatePrevious();
                break;
            case R.id.menu_next:
                mActivity.getWizard().navigateNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}