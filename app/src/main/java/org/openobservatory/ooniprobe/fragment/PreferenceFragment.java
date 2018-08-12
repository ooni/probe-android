package org.openobservatory.ooniprobe.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;

import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener {
	private String rootKey;

	@Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		this.rootKey = rootKey;
	}

	@Override public void onResume() {
		super.onResume();
		setPreferencesFromResource(((PreferenceActivity) getActivity()).getPreference(), android.R.id.content, rootKey);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		getActivity().setTitle(getPreferenceScreen().getTitle());
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
			if (getPreferenceScreen().getPreference(i) instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().getPreference(i);
				editTextPreference.setSummary(editTextPreference.getText());
			}
		if (getPreferenceScreen().getKey().equals(getString(R.string.Settings_AutomatedTesting_Categories_Label)))
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
				if (getPreferenceScreen().getPreference(i) instanceof PreferenceScreen) {
					PreferenceScreen preferenceScreen = (PreferenceScreen) getPreferenceScreen().getPreference(i);
					preferenceScreen.setSummary(preferenceScreen.getSharedPreferences().getBoolean(preferenceScreen.getKey(), false) ? R.string.Settings_Enabled : R.string.Settings_Disabled);
				}
	}

	@Override public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);
		if (preference instanceof EditTextPreference)
			preference.setSummary(sharedPreferences.getString(key, null));
	}

	@Override protected PreferenceFragment newInstance() {
		return new PreferenceFragment();
	}
}