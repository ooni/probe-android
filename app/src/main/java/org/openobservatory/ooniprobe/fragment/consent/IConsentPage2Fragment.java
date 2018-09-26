package org.openobservatory.ooniprobe.fragment.consent;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;

public class IConsentPage2Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO A/B Testing if we want to use a different layout
        return inflater.inflate(R.layout.fragment_ic_page_2, container, false);
    }

}