package org.openobservatory.ooniprobe.fragment.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentOnboarding2Binding;

public class Onboarding2Fragment extends Fragment implements OnboardingDialogPopquizFragment.OnboardingPopquizInterface, OnboardingDialogWarningFragment.OnboardingWarningInterface {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentOnboarding2Binding binding = FragmentOnboarding2Binding.inflate(inflater, container, false);
		binding.bullet1.setText(getString(R.string.bullet, getString(R.string.Onboarding_ThingsToKnow_Bullet_1)));
		binding.bullet2.setText(getString(R.string.bullet, getString(R.string.Onboarding_ThingsToKnow_Bullet_2)));
		binding.bullet3.setText(getString(R.string.bullet, getString(R.string.Onboarding_ThingsToKnow_Bullet_3)));
		binding.master.setOnClickListener(v -> masterClick());
		binding.slave.setOnClickListener(v -> slaveClick());
		return binding.getRoot();
	}

	void masterClick() {
		OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_1_Title, R.string.Onboarding_PopQuiz_1_Question).show(getChildFragmentManager(), null);
	}

	void slaveClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.io/about/risks/")));
	}

	@Override
	public void onPopquizResult(int questionResId, boolean positive) {
		if (questionResId == R.string.Onboarding_PopQuiz_1_Question) {
			if (positive)
				OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_2_Title, R.string.Onboarding_PopQuiz_2_Question).show(getChildFragmentManager(), null);
			else
				OnboardingDialogWarningFragment.newInstance(R.string.Onboarding_PopQuiz_1_Wrong_Paragraph).show(getChildFragmentManager(), null);
		} else if (questionResId == R.string.Onboarding_PopQuiz_2_Question) {
			if (positive)
				getParentFragmentManager().beginTransaction().replace(android.R.id.content, new OnboardingAutoTestFragment()).commit();
			else
				OnboardingDialogWarningFragment.newInstance(R.string.Onboarding_PopQuiz_2_Wrong_Paragraph).show(getChildFragmentManager(), null);
		}
	}

	@Override
	public void onWarningResult(int questionResId) {
		if (questionResId == R.string.Onboarding_PopQuiz_1_Wrong_Paragraph)
			OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_2_Title, R.string.Onboarding_PopQuiz_2_Question).show(getChildFragmentManager(), null);
		else if (questionResId == R.string.Onboarding_PopQuiz_2_Wrong_Paragraph)
			getParentFragmentManager().beginTransaction().replace(android.R.id.content, new OnboardingAutoTestFragment()).commit();
	}
}
