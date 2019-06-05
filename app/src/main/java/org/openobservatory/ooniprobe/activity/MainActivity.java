package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OrchestraTask;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceGlobalFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;

import java.io.Serializable;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.ConfirmDialogFragment;

public class MainActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
	private static final String RES_ITEM = "resItem";
	@BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;

	public static Intent newIntent(Context context, int resItem) {
		return new Intent(context, MainActivity.class).putExtra(RES_ITEM, resItem).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getPreferenceManager().isShowOnboarding()) {
			startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
			finish();
		} else {
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
					case R.id.settings:
						getSupportFragmentManager().beginTransaction().replace(R.id.content, new PreferenceGlobalFragment()).commit();
						return true;
					default:
						return false;
				}
			});
			bottomNavigation.setSelectedItemId(getIntent().getIntExtra(RES_ITEM, R.id.dashboard));
			if (getPreferenceManager().isManualUploadDialog())
				ConfirmDialogFragment.newInstance(
						null,
						getString(R.string.Modal_ManualUpload_Title),
						getString(R.string.Modal_ManualUpload_Paragraph),
						null,
						getString(R.string.Modal_ManualUpload_Enable),
						getString(R.string.Modal_ManualUpload_Disable),
						null
				).show(getSupportFragmentManager(), null);
		}
	}

	@Override protected void onResume() {
		super.onResume();
		new OrchestraTask((Application) getApplication()).execute();
	}

	@Override protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getExtras() != null && intent.getExtras().containsKey(RES_ITEM))
			bottomNavigation.setSelectedItemId(intent.getIntExtra(RES_ITEM, R.id.dashboard));
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		getPreferenceManager().setManualUploadResults(i == DialogInterface.BUTTON_POSITIVE);
	}
}
