package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
	@BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		bottomNavigation.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.dashboard:
					getFragmentManager().beginTransaction().replace(R.id.content, new DashboardFragment()).commit();
					return true;
				case R.id.testResults:
					getFragmentManager().beginTransaction().replace(R.id.content, new DashboardFragment()).commit();
					return true;
				default:
					return false;
			}
		});
		bottomNavigation.setSelectedItemId(R.id.dashboard);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayShowCustomEnabled(true);
			bar.setCustomView(R.layout.logo);
		}
		/* please don't remove
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!preferences.getBoolean("cleanup_unused_files", false)) {
			TestStorage.removeUnusedFiles(this);
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("cleanup_unused_files", true).apply();
		}
	*/
		checkInformedConsent();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				startActivity(PreferenceActivity.newIntent(this, R.xml.preferences_global));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void checkInformedConsent() {
		//TODO preference
		if (!((Application) getApplication()).getPreferenceManager().isShowIntro()) {
			Intent InformedConsentIntent = new Intent(MainActivity.this, InformedConsentActivity.class);
			startActivityForResult(InformedConsentIntent, InformedConsentActivity.REQUEST_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == InformedConsentActivity.REQUEST_CODE) {
			if (resultCode != InformedConsentActivity.RESULT_CODE_COMPLETED) {
				finish();
			} else {
				//TODO preference
				((Application) getApplication()).getPreferenceManager().setShowIntro(false);
			}
		}
	}
}
