package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.databinding.FragmentOnboarding1Binding;

public class Onboarding1Fragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentOnboarding1Binding binding = FragmentOnboarding1Binding.inflate(inflater, container, false);
		binding.master.setOnClickListener(v -> masterClick());
		return binding.getRoot();
	}

	void masterClick() {
		getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding2Fragment()).commit();
	}
}
