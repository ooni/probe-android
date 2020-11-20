package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.CountlyManager;
import org.openobservatory.ooniprobe.common.NotificationService;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceGlobalFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import ly.count.android.sdk.Countly;

public class MainActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    private static final String RES_ITEM = "resItem";
    private static final String ANALYTICS_DIALOG = "analytics";
    public static final String NOTIFICATION_DIALOG = "notification";

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
            if (getPreferenceManager().isShareAnalyticsDialog()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_ShareAnalytics_Title))
                        .withMessage(getString(R.string.Modal_ShareAnalytics_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_SoundsGreat))
                        .withNegativeButton(getString(R.string.Modal_NoThanks))
                        .withExtra(ANALYTICS_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
            //we don't want to flood the user with popups
            else if (getPreferenceManager().getAppOpenCount() == PreferenceManager.NOTIFICATION_DIALOG_COUNT
                    && !getPreferenceManager().isNotifications()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_EnableNotifications_Title))
                        .withMessage(getString(R.string.Modal_EnableNotifications_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .withNegativeButton(getString(R.string.Modal_Cancel))
                        .withExtra(NOTIFICATION_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
        }
    }

	@Override protected void onResume() {
		super.onResume();
		//TODO-SERVICE show RunningActivity
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null){
            if (intent.getExtras().containsKey(RES_ITEM))
                bottomNavigation.setSelectedItemId(intent.getIntExtra(RES_ITEM, R.id.dashboard));
            else if (intent.getExtras().containsKey(NOTIFICATION_DIALOG)){
                new ConfirmDialogFragment.Builder()
                        .withTitle(intent.getExtras().getString("title"))
                        .withMessage(intent.getExtras().getString("message"))
                        .withNegativeButton("")
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .build().show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    public void onConfirmation(Serializable extra, int i) {
        if (extra == null) return;
        if (extra.equals(ANALYTICS_DIALOG))
            getPreferenceManager().setSendAnalytics(i == DialogInterface.BUTTON_POSITIVE);
        else if (extra.equals(NOTIFICATION_DIALOG)) {
            getPreferenceManager().setNotificationsFromDialog(i == DialogInterface.BUTTON_POSITIVE);
            //If positive answer reload consents and init notification
            if (i == DialogInterface.BUTTON_POSITIVE){
                CountlyManager.reloadConsent(((Application) getApplication()).getPreferenceManager());
                NotificationService.initNotification((Application) getApplication());
                CountlyManager.recordEvent("NotificationModal_Accepted");
            }
            else
                CountlyManager.recordEvent("NotificationModal_Declined");
        }
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
