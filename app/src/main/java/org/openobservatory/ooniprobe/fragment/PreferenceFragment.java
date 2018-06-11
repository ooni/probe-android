package org.openobservatory.ooniprobe.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;

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
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			final Preference preference = getPreferenceScreen().getPreference(i);
			if (preference instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) preference;
				preference.setSummary(editTextPreference.getText());
			}
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