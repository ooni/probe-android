package org.openobservatory.netprobe.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.openobservatory.netprobe.R;
import org.openobservatory.netprobe.activity.InformedConsentActivity;

public class IConsentPage3Fragment extends Fragment {


    private InformedConsentActivity mActivity;
    private RadioGroup mRadio1;
    private RadioGroup mRadio2;

    public static IConsentPage3Fragment create() {
        IConsentPage3Fragment atf = new IConsentPage3Fragment();
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

        View v = inflater.inflate(R.layout.fragment_ic_page_3, container, false);
        mRadio1 = (RadioGroup) v.findViewById(R.id.radio_1);
        mRadio2 = (RadioGroup) v.findViewById(R.id.radio_2);

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
                if (mRadio1.getCheckedRadioButtonId() == R.id.answer_1_1 && mRadio2.getCheckedRadioButtonId() == R.id.answer_2_2) {
                    mActivity.showToast(R.string.correct, true);
                    mActivity.getWizard().navigateNext();

                } else {
                    mActivity.showToast(R.string.wrong, false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
