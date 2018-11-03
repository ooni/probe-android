package org.openobservatory.ooniprobe.fragment.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.common.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Onboarding3Fragment extends Fragment {
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_onboarding_3, container, false);
		ButterKnife.bind(this, v);
		return v;
	}

	@OnClick(R.id.master) void masterClick() {
		((Application) getActivity().getApplication()).getPreferenceManager().setShowOnboarding(false);
		startActivity(MainActivity.newIntent(getActivity(), R.id.dashboard));
		getActivity().finish();
	}

	@OnClick(R.id.slave) void slaveClick() {
		((Application) getActivity().getApplication()).getPreferenceManager().setShowOnboarding(false);
		startActivity(MainActivity.newIntent(getActivity(), R.id.settings));
		getActivity().finish();
	}
}
