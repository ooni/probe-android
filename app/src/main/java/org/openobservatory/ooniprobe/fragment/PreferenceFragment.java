package org.openobservatory.ooniprobe.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;

import java.io.Serializable;

import androidx.annotation.IdRes;
import androidx.annotation.XmlRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import localhost.toolkit.app.ConfirmDialogFragment;
import localhost.toolkit.app.MessageDialogFragment;
import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener, ConfirmDialogFragment.OnConfirmedListener {
	private static final String PREFERENCES_RES_ID = "preferencesResId";
	private static final String PREFERENCES_CONTAINER_RES_ID = "preferencesContainerResId";
	private String rootKey;

	public static PreferenceFragment newInstance(@XmlRes int preferencesResId, @IdRes int preferencesContainerResId) {
		PreferenceFragment fragment = new PreferenceFragment();
		fragment.setArguments(new Bundle());
		fragment.getArguments().putInt(PREFERENCES_RES_ID, preferencesResId);
		fragment.getArguments().putInt(PREFERENCES_CONTAINER_RES_ID, preferencesContainerResId);
		return fragment;
	}

	@Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		this.rootKey = rootKey;
	}

	@Override public void onResume() {
		super.onResume();
		setPreferencesFromResource(getArguments().getInt(PREFERENCES_RES_ID), getArguments().getInt(PREFERENCES_CONTAINER_RES_ID), rootKey);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		getActivity().setTitle(getPreferenceScreen().getTitle());
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			if (getPreferenceScreen().getPreference(i) instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().getPreference(i);
				editTextPreference.setSummary(editTextPreference.getText());
			}
			if (getPreferenceScreen().getPreference(i).getKey().equals(getString(R.string.Settings_AutomatedTesting_Categories_Label))) {
				int enabledCategory = ((Application) getActivity().getApplication()).getPreferenceManager().countEnabledCategory();
				getPreferenceScreen().getPreference(i).setSummary("" + enabledCategory);
			}
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
			if (key.equals(getString(R.string.max_runtime)) && value != null && !TextUtils.isDigitsOnly(value)) {
				MessageDialogFragment.newInstance(getString(R.string.Modal_Error), getString(R.string.Modal_OnlyDigits), false).show(getFragmentManager(), null);
				sharedPreferences.edit().remove(key).apply();
				ExtendedPreferenceFragment fragment = newConcreteInstance();
				fragment.getArguments().putString(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
				getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
			}
		}
	}

	@Override protected PreferenceFragment newConcreteInstance() {
		return PreferenceFragment.newInstance(getArguments().getInt(PREFERENCES_RES_ID), getArguments().getInt(PREFERENCES_CONTAINER_RES_ID));
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_NEGATIVE && serializable.equals(getString(R.string.include_cc))) {
			getPreferenceScreen().getSharedPreferences().edit().remove((String) serializable).apply();
			ExtendedPreferenceFragment fragment = newConcreteInstance();
			Bundle args = new Bundle();
			args.putString(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
			fragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		}
	}
}