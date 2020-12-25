package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Onboarding1Fragment extends Fragment {
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_onboarding_1, container, false);
		ButterKnife.bind(this, v);
		return v;
	}

	@OnClick(R.id.master) void masterClick() {
		getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding2Fragment()).commit();
	}
}
