package org.openobservatory.ooniprobe.fragment;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;

import org.openobservatory.ooniprobe.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class MyPreferenceFragment extends ExtendedPreferenceFragment<MyPreferenceFragment> {
	private static final List<Integer> PREF_REQUIRE_REFRESH = Collections.singletonList(R.string.key_websiteTypes);
	private static final List<int[]> PREF_TO_REFRESH = Arrays.asList(
			new int[]{R.string.key_screen_aldr, R.string.key_switch_aldr},
			new int[]{R.string.key_screen_rel, R.string.key_switch_rel},
			new int[]{R.string.key_screen_porn, R.string.key_switch_porn},
			new int[]{R.string.key_screen_prov, R.string.key_switch_prov},
			new int[]{R.string.key_screen_polr, R.string.key_switch_polr},
			new int[]{R.string.key_screen_humr, R.string.key_switch_humr}
	);
	private String rootKey;
	private boolean refresh;

	@Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		if (rootKey != null)
			for (int prefScreen : PREF_REQUIRE_REFRESH)
				if (rootKey.equals(getString(prefScreen)))
					refresh = true;
		if (refresh)
			this.rootKey = rootKey;
		else
			setPreferencesFromResource(R.xml.preferences, android.R.id.content, rootKey);
	}

	@Override public void onResume() {
		super.onResume();
		if (refresh) {
			setPreferencesFromResource(R.xml.preferences, android.R.id.content, rootKey);
			for (int[] pref : PREF_TO_REFRESH) {
				Preference prefScreen = findPreference(getString(pref[0]));
				SwitchPreference switchPreference = (SwitchPreference) findPreference(getString(pref[1]));
				if (prefScreen != null && switchPreference != null)
					prefScreen.setSummary(switchPreference.isChecked() ? R.string.enabled : R.string.disabled);
			}
		}
		getActivity().setTitle(getPreferenceScreen().getTitle());
	}

	@Override protected MyPreferenceFragment newInstance() {
		return new MyPreferenceFragment();
	}
}


