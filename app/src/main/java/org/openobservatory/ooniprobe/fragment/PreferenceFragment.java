package org.openobservatory.ooniprobe.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import localhost.toolkit.app.ConfirmDialogFragment;
import localhost.toolkit.app.MessageDialogFragment;
import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener, ConfirmDialogFragment.OnConfirmedListener {
	private String rootKey;
	private List<String> intPref;

	@Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		this.rootKey = rootKey;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intPref = Arrays.asList(getResources().getStringArray(R.array.integerPreferences));
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
		if (key.equals(getString(R.string.include_cc)) && !sharedPreferences.getBoolean(key, true))
			ConfirmDialogFragment.newInstance(key, getString(R.string.Settings_Sharing_IncludeCountryCode), getString(R.string.Settings_Sharing_IncludeCountryCode_PopUp)).show(getChildFragmentManager(), null);
		else if (preference instanceof EditTextPreference) {
			String value = sharedPreferences.getString(key, null);
			preference.setSummary(value);
			if (intPref.contains(key) && value != null && !TextUtils.isDigitsOnly(value)) {
				MessageDialogFragment.newInstance(getString(R.string.Modal_Error), getString(R.string.Modal_OnlyDigits), false).show(getFragmentManager(), null);
				sharedPreferences.edit().remove(key).apply();
				ExtendedPreferenceFragment fragment = newInstance();
				Bundle args = new Bundle();
				args.putString(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
				fragment.setArguments(args);
				getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			}
		}
	}

	@Override protected PreferenceFragment newInstance() {
		return new PreferenceFragment();
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_NEGATIVE && serializable.equals(getString(R.string.include_cc))) {
			getPreferenceScreen().getSharedPreferences().edit().remove((String) serializable).apply();
			ExtendedPreferenceFragment fragment = newInstance();
			Bundle args = new Bundle();
			args.putString(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
			fragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		}
	}
}