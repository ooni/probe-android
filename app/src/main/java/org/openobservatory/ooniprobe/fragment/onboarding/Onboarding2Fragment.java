package org.openobservatory.ooniprobe.fragment.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Onboarding2Fragment extends Fragment implements OnboardingDialogPopquizFragment.OnboardingPopquizInterface, OnboardingDialogWarningFragment.OnboardingWarningInterface {
	@BindView(R.id.bullet1) TextView bullet1;
	@BindView(R.id.bullet2) TextView bullet2;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_onboarding_2, container, false);
		ButterKnife.bind(this, v);
		bullet1.setText(getString(R.string.bullet, getString(R.string.Onboarding_ThingsToKnow_Bullet_1)));
		bullet2.setText(getString(R.string.bullet, getString(R.string.Onboarding_ThingsToKnow_Bullet_2)));
		return v;
	}

	@OnClick(R.id.master) void masterClick() {
		OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_1_Title, R.string.Onboarding_PopQuiz_1_Question).show(getChildFragmentManager(), null);
	}

	@OnClick(R.id.slave) void slaveClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.io/about/risks/")));
	}

	@Override public void onPopquizResult(int questionResId, boolean positive) {
		if (questionResId == R.string.Onboarding_PopQuiz_1_Question) {
			if (positive)
				OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_2_Title, R.string.Onboarding_PopQuiz_2_Question).show(getChildFragmentManager(), null);
			else
				OnboardingDialogWarningFragment.newInstance(R.string.Onboarding_PopQuiz_1_Wrong_Paragraph).show(getChildFragmentManager(), null);
		} else if (questionResId == R.string.Onboarding_PopQuiz_2_Question) {
			if (positive)
				getFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
			else
				OnboardingDialogWarningFragment.newInstance(R.string.Onboarding_PopQuiz_2_Wrong_Paragraph).show(getChildFragmentManager(), null);
		}
	}

	@Override public void onWarningResult(int questionResId) {
		if (questionResId == R.string.Onboarding_PopQuiz_1_Wrong_Paragraph)
			OnboardingDialogPopquizFragment.newInstance(R.string.Onboarding_PopQuiz_2_Title, R.string.Onboarding_PopQuiz_2_Question).show(getChildFragmentManager(), null);
		else if (questionResId == R.string.Onboarding_PopQuiz_2_Wrong_Paragraph)
			getFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
	}
}
