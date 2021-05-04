package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingCrashFragment extends Fragment {
    @Nullable
    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_onboarding_crash, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.master) void masterClick() {
        ThirdPartyServices.acceptCrash((Application) getActivity().getApplication());
        getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
    }

    @OnClick(R.id.slave) void slaveClick() {
        getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
    }

}
