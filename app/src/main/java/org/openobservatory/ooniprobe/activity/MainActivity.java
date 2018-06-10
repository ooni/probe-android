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
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.MyPreferenceFragment;

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
					getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();
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
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				startActivity(new Intent(this, PreferenceActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
