package org.openobservatory.ooniprobe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.fragment.onboarding.Onboarding1Fragment;

import java.io.Serializable;

import javax.inject.Inject;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class OnboardingActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
	@Inject
	PreferenceManager preferenceManager;
	public static final String BATTERY_DIALOG = "battery_optimization";

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding1Fragment()).commit();
	}

	@Override
	public void onConfirmation(Serializable extra, int i) {
		if (extra == null) return;
		if (extra.equals(BATTERY_DIALOG)) {
			preferenceManager.setNotificationsFromDialog(i == DialogInterface.BUTTON_POSITIVE);
			if (i == DialogInterface.BUTTON_POSITIVE) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + getPackageName()));
				startActivityForResult(intent, PreferenceManager.IGNORE_OPTIMIZATION_REQUEST);
			}
		}
	}
}
