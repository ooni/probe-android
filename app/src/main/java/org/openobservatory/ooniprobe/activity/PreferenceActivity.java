package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.openobservatory.ooniprobe.fragment.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

public class PreferenceActivity extends AbstractActivity {
	public static Intent newIntent(Context context, @XmlRes int preferenceResId, String rootKey) {
		return new Intent(context, PreferenceActivity.class).putExtra(PreferenceFragment.ARG_PREFERENCES_RES_ID, preferenceResId).putExtra(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, PreferenceFragment.newInstance(getIntent().getIntExtra(PreferenceFragment.ARG_PREFERENCES_RES_ID, 2), android.R.id.content, getIntent().getStringExtra(PreferenceFragment.ARG_PREFERENCE_ROOT))).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
