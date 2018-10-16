package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.openobservatory.ooniprobe.fragment.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

public class PreferenceActivity extends AbstractActivity implements PreferenceFragment.PreferenceInterface {
	private static final String PREFERENCE_RES_ID = "preferenceResId";

	public static Intent newIntent(Context context, @XmlRes int preferenceResId) {
		return new Intent(context, PreferenceActivity.class).putExtra(PREFERENCE_RES_ID, preferenceResId);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
	}

	@Override @XmlRes public int getPreferenceResId() {
		return getIntent().getIntExtra(PREFERENCE_RES_ID, 0);
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
