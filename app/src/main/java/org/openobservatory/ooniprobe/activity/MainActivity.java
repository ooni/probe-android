package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OrchestraTask;
import org.openobservatory.ooniprobe.fragment.DashboardFragment;
import org.openobservatory.ooniprobe.fragment.PreferenceGlobalFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;
import ly.count.android.sdk.DeviceId;

public class MainActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    private static final String RES_ITEM = "resItem";
    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;

    public static Intent newIntent(Context context, int resItem) {
        return new Intent(context, MainActivity.class).putExtra(RES_ITEM, resItem).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CountlyConfig config = new CountlyConfig()
                .setAppKey("fd78482a10e95fd471925399adbcb8ae1a45661f")
                .setContext(this)
                .setDeviceId(null)
                .setIdMode(DeviceId.Type.ADVERTISING_ID)
                .setServerURL("https://mia-countly-test.ooni.nu")
                //.setLoggingEnabled(!BuildConfig.DEBUG)
                .setLoggingEnabled(true)
                .setViewTracking(true)
                .setHttpPostForced(true)
                .enableCrashReporting();
        Countly.sharedInstance().init(config);
        /*
        Deprecated code
        Countly.sharedInstance().init(this, "https://mia-countly-test.ooni.nu", "fd78482a10e95fd471925399adbcb8ae1a45661f", null, DeviceId.Type.ADVERTISING_ID);
        Countly.sharedInstance().initMessaging(this, MainActivity.class, "951667061699", Countly.CountlyMessagingMode.PRODUCTION);
        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();
        */
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
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_ManualUpload_Title))
                        .withMessage(getString(R.string.Modal_ManualUpload_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_ManualUpload_Enable))
                        .withNegativeButton(getString(R.string.Modal_ManualUpload_Disable))
                        .build().show(getSupportFragmentManager(), null);
        }
    }

	@Override protected void onResume() {
		super.onResume();
		new OrchestraTask((Application) getApplication()).execute();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey(RES_ITEM))
            bottomNavigation.setSelectedItemId(intent.getIntExtra(RES_ITEM, R.id.dashboard));
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        getPreferenceManager().setManualUploadResults(i == DialogInterface.BUTTON_POSITIVE);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    public void onStop()
    {
        Countly.sharedInstance().onStop();
        super.onStop();
    }
}
