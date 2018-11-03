package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;

import org.openobservatory.ooniprobe.fragment.onboarding.Onboarding1Fragment;

import androidx.annotation.Nullable;

public class OnboardingActivity extends AbstractActivity {
	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding1Fragment()).commit();
	}
}
