package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.fragment.onboarding.Onboarding1Fragment;

public class OnboardingActivity extends AbstractActivity {
	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding1Fragment()).commit();
	}


}
