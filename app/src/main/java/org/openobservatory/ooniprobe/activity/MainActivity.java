package org.openobservatory.ooniprobe.activity;

import static org.openobservatory.ooniprobe.common.service.RunTestService.CHANNEL_ID;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.NotificationUtility;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.databinding.ActivityMainBinding;
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
    public static final String AUTOTEST_DIALOG = "automatic_testing";
    public static final String BATTERY_DIALOG = "battery_optimization";

    private ActivityMainBinding binding;

    @Inject
    UpdatesNotificationManager notificationManager;

    @Inject
    PreferenceManager preferenceManager;

    private ActivityResultLauncher<String> requestPermissionLauncher;

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
            ButterKnife.bind(this);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
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
            binding.bottomNavigation.setSelectedItemId(getIntent().getIntExtra(RES_ITEM, R.id.dashboard));
            if (notificationManager.shouldShowAutoTest()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_Autorun_Modal_Title))
                        .withMessage(getString(R.string.Modal_Autorun_Modal_Text)
                                + "\n" + getString(R.string.Modal_Autorun_Modal_Text_Android))
                        .withPositiveButton(getString(R.string.Modal_SoundsGreat))
                        .withNegativeButton(getString(R.string.Modal_NotNow))
                        .withNeutralButton(getString(R.string.Modal_DontAskAgain))
                        .withExtra(AUTOTEST_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            } else if (notificationManager.shouldShow()) {
                new ConfirmDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_EnableNotifications_Title))
                        .withMessage(getString(R.string.Modal_EnableNotifications_Paragraph))
                        .withPositiveButton(getString(R.string.Modal_SoundsGreat))
                        .withNegativeButton(getString(R.string.Modal_NotNow))
                        .withNeutralButton(getString(R.string.Modal_DontAskAgain))
                        .withExtra(NOTIFICATION_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
            ThirdPartyServices.checkUpdates(this);
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
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                (result) -> {
                    if (!result) {
                        Snackbar.make(
                                binding.getRoot(),
                                "Please grant Notification permission from App Settings",
                                Snackbar.LENGTH_LONG
                                ).setAction(R.string.Settings_Title, view -> {
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    //for Android 5-7
                                    intent.putExtra("app_package", getPackageName());
                                    intent.putExtra("app_uid", getApplicationInfo().uid);

                                    // for Android 8 and above
                                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                                    startActivity(intent);
                                }).show();
                    }
                }
        );
        NotificationUtility.setChannel(getApplicationContext(), CHANNEL_ID, getString(R.string.Settings_AutomatedTesting_Label), false, false, false);
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null){
            if (intent.getExtras().containsKey(RES_ITEM))
                binding.bottomNavigation.setSelectedItemId(intent.getIntExtra(RES_ITEM, R.id.dashboard));
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
        if (extra.equals(AUTOTEST_DIALOG)) {
            preferenceManager.setNotificationsFromDialog(i == DialogInterface.BUTTON_POSITIVE);
            if (i == DialogInterface.BUTTON_POSITIVE){
                //For API < 23 we ignore battery optimization
                boolean isIgnoringBatteryOptimizations = true;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
                }
                if(!isIgnoringBatteryOptimizations){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PreferenceManager.IGNORE_OPTIMIZATION_REQUEST);
                }
                else {
                    preferenceManager.enableAutomatedTesting();
                    ServiceUtil.scheduleJob(this);
                }
            }
            else if (i == DialogInterface.BUTTON_NEUTRAL){
                preferenceManager.disableAskAutomaticTestDialog();
            }
        }
        if (extra.equals(BATTERY_DIALOG)) {
            preferenceManager.setNotificationsFromDialog(i == DialogInterface.BUTTON_POSITIVE);
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PreferenceManager.IGNORE_OPTIMIZATION_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PreferenceManager.IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //For API < 23 we ignore battery optimization
            boolean isIgnoringBatteryOptimizations = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            }
            if (isIgnoringBatteryOptimizations) {
                preferenceManager.enableAutomatedTesting();
                ServiceUtil.scheduleJob(this);
            }
            else {
                new ConfirmDialogFragment.Builder()
                        .withMessage(getString(R.string.Modal_Autorun_BatteryOptimization))
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .withNegativeButton(getString(R.string.Modal_Cancel))
                        .withExtra(BATTERY_DIALOG)
                        .build().show(getSupportFragmentManager(), null);
            }
        }
        else if (requestCode == PreferenceManager.ASK_UPDATE_APP) {
            if (resultCode != RESULT_OK) {
                //We don't need to check the result for now
            }
        }
    }
}
