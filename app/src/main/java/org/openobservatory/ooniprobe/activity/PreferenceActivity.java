package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import android.view.MenuItem;

import org.openobservatory.ooniprobe.fragment.PreferenceFragment;

public class PreferenceActivity extends AbstractActivity {
	public static final String PREFERENCE = "preference";

	public static Intent newIntent(Context context, @XmlRes int preference) {
		return new Intent(context, PreferenceActivity.class).putExtra(PREFERENCE, preference);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
	}

	public int getPreference() {
		return getIntent().getIntExtra(PREFERENCE, 0);
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
