package org.openobservatory.ooniprobe.fragment.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openobservatory.ooniprobe.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Onboarding2Fragment extends Fragment {
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_onboarding_2, container, false);
		ButterKnife.bind(this, v);
		return v;
	}

	@OnClick(R.id.master) void masterClick() {
		getFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).addToBackStack(null).commit();
	}

	@OnClick(R.id.slave) void slaveClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.io/about/risks/")));
	}
}
