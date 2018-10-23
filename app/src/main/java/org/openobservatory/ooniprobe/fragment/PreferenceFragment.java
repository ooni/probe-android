package org.openobservatory.ooniprobe.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.samskivert.mustache.Mustache;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;
import org.openobservatory.ooniprobe.common.Application;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.IdRes;
import androidx.annotation.XmlRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import localhost.toolkit.app.ConfirmDialogFragment;
import localhost.toolkit.app.MessageDialogFragment;
import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener, ConfirmDialogFragment.OnConfirmedListener {
	public static final String ARG_PREFERENCES_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.PREF_RES_ID";
	public static final String ARG_CONTAINER_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.CONTAINER_VIEW_ID";
	private String rootKey;

	public static PreferenceFragment newInstance(@XmlRes int preferencesResId, @IdRes int preferencesContainerResId, String rootKey) {
		PreferenceFragment fragment = new PreferenceFragment();
		fragment.setArguments(new Bundle());
		fragment.getArguments().putInt(ARG_PREFERENCES_RES_ID, preferencesResId);
		fragment.getArguments().putInt(ARG_CONTAINER_RES_ID, preferencesContainerResId);
		fragment.getArguments().putString(ARG_PREFERENCE_ROOT, rootKey);
		return fragment;
	}

	@Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		this.rootKey = rootKey;
	}

	@Override public void onResume() {
		super.onResume();
		setPreferencesFromResource(getArguments().getInt(ARG_PREFERENCES_RES_ID), getArguments().getInt(ARG_CONTAINER_RES_ID), getArguments().getString(ARG_PREFERENCE_ROOT));
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		getActivity().setTitle(getPreferenceScreen().getTitle());
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			if (getPreferenceScreen().getPreference(i) instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().getPreference(i);
				editTextPreference.setSummary(editTextPreference.getText());
			}
			if (getString(R.string.Settings_AutomatedTesting_Categories_Label).equals(getPreferenceScreen().getPreference(i).getKey())) {
				HashMap<String, String> data = new HashMap<>();
				data.put("Count", ((Application) getActivity().getApplication()).getPreferenceManager().countEnabledCategory().toString());
				getPreferenceScreen().getPreference(i).setSummary(Mustache.compiler().compile(getString(R.string.Settings_AutomatedTesting_Categories_Subtitle)).execute(data));
			}
		}
	}

	@Override public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
		if (getActivity() instanceof MainActivity) {
			startActivity(PreferenceActivity.newIntent(getActivity(), getArguments().getInt(ARG_PREFERENCES_RES_ID), preferenceScreen.getKey()));
			return true;
		} else
			return super.onPreferenceStartScreen(preferenceFragmentCompat, preferenceScreen);
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
				getFragmentManager().beginTransaction().replace(android.R.id.content, newConcreteInstance(rootKey)).commit();
			}
		}
	}

	@Override protected PreferenceFragment newConcreteInstance(String rootKey) {
		return PreferenceFragment.newInstance(getArguments().getInt(ARG_PREFERENCES_RES_ID), getArguments().getInt(ARG_CONTAINER_RES_ID), rootKey);
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_NEGATIVE && serializable.equals(getString(R.string.include_cc))) {
			getPreferenceScreen().getSharedPreferences().edit().remove((String) serializable).apply();
			getFragmentManager().beginTransaction().replace(android.R.id.content, newConcreteInstance(rootKey)).commit();
		}
	}
}