package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.databinding.FragmentOnboardingCrashBinding;

public class OnboardingCrashFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentOnboardingCrashBinding binding = FragmentOnboardingCrashBinding.inflate(inflater, container, false);
        binding.master.setOnClickListener(v -> masterClick());
        binding.slave.setOnClickListener(v -> slaveClick());
        return binding.getRoot();
    }

    void masterClick() {
        ThirdPartyServices.acceptCrash((Application) getActivity().getApplication());
        getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
    }

    void slaveClick() {
        getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
    }

}
