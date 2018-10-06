package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AbstractActivity {
	public static final String RES_ITEM = "resItem";
	@BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;

	public static Intent newIntent(Context context, int resItem) {
		return new Intent(context, MainActivity.class).putExtra(RES_ITEM, resItem).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		bottomNavigation.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.dashboard:
					getSupportFragmentManager().beginTransaction().replace(R.id.content, new DashboardFragment()).commit();
					return true;
				case R.id.testResults:
					getSupportFragmentManager().beginTransaction().replace(R.id.content, new ResultListFragment()).commit();
					return true;
				default:
					return false;
			}
		});
		bottomNavigation.setSelectedItemId(getIntent().getIntExtra(RES_ITEM, R.id.dashboard));

		/*
		//TODO
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!preferences.getBoolean("cleanup_unused_files", false)) {
			OldTestStorage.removeUnusedFiles(this);
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("cleanup_unused_files", true).apply();
		}*/
		checkInformedConsent();
	}

	public void checkInformedConsent() {
		if (getPreferenceManager().isShowIntro())
			startActivityForResult(new Intent(MainActivity.this, InformedConsentActivity.class), InformedConsentActivity.REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == InformedConsentActivity.REQUEST_CODE) {
			if (resultCode != InformedConsentActivity.RESULT_CODE_COMPLETED) {
				finish();
			} else {
				getPreferenceManager().setShowIntro(false);
			}
		}
	}
}
