package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.databinding.FragmentOnboarding3Binding;
import ru.noties.markwon.Markwon;

import javax.inject.Inject;

public class Onboarding3Fragment extends Fragment {

	@Inject PreferenceManager preferenceManager;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((Application) getActivity().getApplication()).getFragmentComponent().inject(this);

		FragmentOnboarding3Binding binding = FragmentOnboarding3Binding.inflate(inflater, container, false);
		binding.bullet1.setText(getString(R.string.bullet, getString(R.string.Onboarding_DefaultSettings_Bullet_1)));
		binding.bullet2.setText(getString(R.string.bullet, getString(R.string.Onboarding_DefaultSettings_Bullet_2)));
		binding.bullet3.setText(getString(R.string.bullet, getString(R.string.Onboarding_DefaultSettings_Bullet_3)));
		Markwon.setMarkdown(binding.paragraph, getString(R.string.Onboarding_DefaultSettings_Paragraph));

		binding.master.setOnClickListener(v -> masterClick());
		binding.slave.setOnClickListener(v -> slaveClick());

		return binding.getRoot();
	}

	void masterClick() {
		preferenceManager.setShowOnboarding(false);
		ThirdPartyServices.reloadConsents((Application) getActivity().getApplication());
		startAutoTestIfNeeded();
		startActivity(MainActivity.newIntent(getActivity(), R.id.dashboard));
		getActivity().finish();
	}

	void slaveClick() {
		preferenceManager.setShowOnboarding(false);
		startAutoTestIfNeeded();
		startActivity(MainActivity.newIntent(getActivity(), R.id.settings));
		getActivity().finish();
	}

	private void startAutoTestIfNeeded(){
		if	(preferenceManager.isAutomaticTestEnabled())
			ServiceUtil.scheduleJob(getActivity());
	}
}
