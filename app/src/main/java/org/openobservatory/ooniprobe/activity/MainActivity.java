package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceGlobalFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import ly.count.android.sdk.Countly;

public class MainActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    private static final String RES_ITEM = "resItem";
    private static final String MANUAL_UPLOAD_DIALOG = "manual_upload";
    private static final String ANALYTICS_DIALOG = "analytics";

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;

    public static Intent newIntent(Context context, int resItem) {
        return new Intent(context, MainActivity.class).putExtra(RES_ITEM, resItem).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPreferenceManager().isShowOnboarding() && !isUITestRunning()) {
            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
            finish();
        }
        else {
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
            if (isUITestRunning()) {
                return;
            }
            //These cases are not mutually exclusive. When a user upgrade if one or both modal has never been showed it will be.
            if (getPreferenceManager().isManualUploadDialog()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_ManualUpload_Title))
                        .withMessage(getString(R.string.Modal_ManualUpload_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_ManualUpload_Enable))
                        .withNegativeButton(getString(R.string.Modal_ManualUpload_Disable))
                        .withExtra(MANUAL_UPLOAD_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
            if (getPreferenceManager().isShareAnalyticsDialog()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_ShareAnalytics_Title))
                        .withMessage(getString(R.string.Modal_ShareAnalytics_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_ShareAnalytics_Enable))
                        .withNegativeButton(getString(R.string.Modal_ShareAnalytics_Disable))
                        .withExtra(ANALYTICS_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
        }
    }

	@Override protected void onResume() {
		super.onResume();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey(RES_ITEM))
            bottomNavigation.setSelectedItemId(intent.getIntExtra(RES_ITEM, R.id.dashboard));
    }

    @Override
    public void onConfirmation(Serializable extra, int i) {
        if (extra.equals(MANUAL_UPLOAD_DIALOG))
            getPreferenceManager().setManualUploadResults(i == DialogInterface.BUTTON_POSITIVE);
        else if (extra.equals(ANALYTICS_DIALOG))
            getPreferenceManager().setSendAnalytics(i == DialogInterface.BUTTON_POSITIVE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    public void onStop() {
        Countly.sharedInstance().onStop();
        super.onStop();
    }

    public boolean isUITestRunning() {
        try {
            Class.forName("org.openobservatory.ooniprobe.AutomateScreenshotsTest");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
