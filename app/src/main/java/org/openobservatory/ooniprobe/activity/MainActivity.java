package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.domain.UpdatesNotificationManager;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceGlobalFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class MainActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    private static final String RES_ITEM = "resItem";
    public static final String NOTIFICATION_DIALOG = "notification";

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;

    @Inject
    UpdatesNotificationManager notificationManager;

    @Inject
    PreferenceManager preferenceManager;

    public static Intent newIntent(Context context, int resItem) {
        return new Intent(context, MainActivity.class).putExtra(RES_ITEM, resItem).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        if (preferenceManager.isShowOnboarding()) {
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
            if (notificationManager.shouldShow()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_EnableNotifications_Title))
                        .withMessage(getString(R.string.Modal_EnableNotifications_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .withNegativeButton(getString(R.string.Modal_NoThanks))
                        .withNeutralButton(getString(R.string.Modal_DontAskAgain))
                        .withExtra(NOTIFICATION_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }

        }

        if (android.os.Build.VERSION.SDK_INT >= 29){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else{
            if (preferenceManager.isDarkTheme()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

    }

	@Override protected void onResume() {
		super.onResume();
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
        if (extra.equals(NOTIFICATION_DIALOG)) {
            notificationManager.getUpdates(i == DialogInterface.BUTTON_POSITIVE);

            //If positive answer reload consents and init notification
            if (i == DialogInterface.BUTTON_POSITIVE){
                ThirdPartyServices.reloadConsents((Application) getApplication());
            }
            else if (i == DialogInterface.BUTTON_NEUTRAL){
                notificationManager.disableAskNotificationDialog();
            }
        }
    }
}
