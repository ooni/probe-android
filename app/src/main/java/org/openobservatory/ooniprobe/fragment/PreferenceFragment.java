package org.openobservatory.ooniprobe.fragment;

import static org.openobservatory.ooniprobe.common.PreferenceManager.COUNT_WEBSITE_CATEGORIES;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.PreferenceActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.model.database.Measurement;

import localhost.toolkit.app.fragment.MessageDialogFragment;
import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ARG_PREFERENCES_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.PREF_RES_ID";
    private static final String ARG_CONTAINER_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.CONTAINER_VIEW_ID";
    private String rootKey;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public static PreferenceFragment newInstance(@XmlRes int preferencesResId, @IdRes int preferencesContainerResId, String rootKey) {
        PreferenceFragment fragment = new PreferenceFragment();
        fragment.setArguments(new Bundle());
        fragment.getArguments().putInt(ARG_PREFERENCES_RES_ID, preferencesResId);
        fragment.getArguments().putInt(ARG_CONTAINER_RES_ID, preferencesContainerResId);
        fragment.getArguments().putString(ARG_PREFERENCE_ROOT, rootKey);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (result) -> {});
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.rootKey = rootKey;
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        assert getArguments() != null;
        super.onResume();
        setPreferencesFromResource(getArguments().getInt(ARG_PREFERENCES_RES_ID), getArguments().getInt(ARG_CONTAINER_RES_ID), getArguments().getString(ARG_PREFERENCE_ROOT));
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        getActivity().setTitle(getPreferenceScreen().getTitle());

        if (android.os.Build.VERSION.SDK_INT >= 29){
            Preference themeDark = findPreference(getString(R.string.theme_enabled));
            if(themeDark != null) {
                themeDark.setEnabled(false);
                themeDark.setVisible(false);
            }
        }

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            if (getPreferenceScreen().getPreference(i) instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().getPreference(i);
                editTextPreference.setSummary(editTextPreference.getText());
            }
            if (getString(R.string.Settings_Websites_Categories_Label).equals(getPreferenceScreen().getPreference(i).getKey())) {
                String count = ((Application) getActivity().getApplication()).getPreferenceManager().countEnabledCategory().toString();
                getPreferenceScreen().getPreference(i).setSummary(getString(R.string.Settings_Websites_Categories_Description, count));
            }
            if (getString(R.string.automated_testing_enabled).equals(getPreferenceScreen().getPreference(i).getKey())) {
                long autorun_num = ((Application) getActivity().getApplication()).getPreferenceManager().getAutorun();
                String autorun_date = ((Application) getActivity().getApplication()).getPreferenceManager().getAutorunDate();
                getPreferenceScreen().getPreference(i).setSummary(getString(R.string.Settings_AutomatedTesting_RunAutomatically_Number, String.valueOf(autorun_num)) +
                        "\n" + getString(R.string.Settings_AutomatedTesting_RunAutomatically_DateLast, autorun_date));
            }
        }
        Preference pref = findPreference(getString(R.string.send_email));
        if (pref != null)
            pref.setOnPreferenceClickListener(preference -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.shareEmailTo)));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareSubject, BuildConfig.VERSION_NAME));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.Settings_SendEmail_Message) +
                        "\n\n\nMANUFACTURER: " + Build.MANUFACTURER + "\nMODEL: " + Build.MODEL + "\nBOARD: " + Build.BOARD + "\nTIME: " + Build.TIME);
                try {
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.Settings_SendEmail_Label)));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.Settings_SendEmail_Error, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        setStorage();
        hidePreferences();
    }

    public void setStorage(){
        Preference storage = findPreference(getString(R.string.storage_usage));
        if (storage != null){
            storage.setSummary(FileUtils.byteCountToDisplaySize(Measurement.getStorageUsed(getContext())));
        }
    }

    public void disableScheduler(){
        Preference automated_testing_enabled = findPreference(getString(R.string.automated_testing_enabled));
        if (automated_testing_enabled != null){
            automated_testing_enabled.setEnabled(false);
        }
        ((Application) getActivity().getApplication()).getPreferenceManager().disableAutomaticTest();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        if (getActivity() instanceof MainActivity) {
            startActivity(PreferenceActivity.newIntent(getActivity(), getArguments().getInt(ARG_PREFERENCES_RES_ID), preferenceScreen.getKey()));
            return true;
        } else
            return super.onPreferenceStartScreen(preferenceFragmentCompat, preferenceScreen);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (key.equals(getString(R.string.automated_testing_enabled))) {
            if (sharedPreferences.getBoolean(key, false)) {
                if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.POST_NOTIFICATIONS
                    );

                    if (showRationale) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    } else {
                        ServiceUtil.launchNotificationSettings(getContext());
                    }
                }
                //For API < 23 we ignore battery optimization
                boolean isIgnoringBatteryOptimizations = true;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                    isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
                }
                if(!isIgnoringBatteryOptimizations){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, PreferenceManager.IGNORE_OPTIMIZATION_REQUEST);
                }
                else {
                    ServiceUtil.scheduleJob(getContext());
                }
            }
            else {
                ServiceUtil.stopJob(getContext());
            }
        }
        if (key.equals(getString(R.string.automated_testing_charging)) ||
                key.equals(getString(R.string.automated_testing_wifionly))){
            //stop and re-enable scheduler in case of wifi charging option changed

            ServiceUtil.stopJob(getContext());
            ServiceUtil.scheduleJob(getContext());
        }
        if (key.equals(getString(R.string.send_crash)) ||
                key.equals(getString(R.string.notifications_enabled))){
            ThirdPartyServices.reloadConsents((Application) getActivity().getApplication());
        }
        else if (preference instanceof EditTextPreference) {
            String value = sharedPreferences.getString(key, null);
            preference.setSummary(value);
            if (key.equals(getString(R.string.max_runtime)) && value != null && !TextUtils.isDigitsOnly(value)) {
                new MessageDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_Error))
                        .withMessage(getString(R.string.Modal_OnlyDigits))
                        .build().show(getChildFragmentManager(), null);
                sharedPreferences.edit().remove(key).apply();
                EditTextPreference p = findPreference(key);
                if (p != null)
                    p.setText("");
            }
        }

        if (key.equals(getString(R.string.theme_enabled)) || key.equals(getString(R.string.language_setting))) {
            Toast.makeText(getActivity(), "Please restart the app for apply changes.", Toast.LENGTH_LONG).show();
            getActivity().finishAffinity();
        }
        hidePreferences();
    }

    private void hidePreferences(){
        PreferenceManager pm = ((Application) getActivity().getApplication()).getPreferenceManager();
        EditTextPreference p_runtime = findPreference(getString(R.string.max_runtime));
        if (p_runtime != null){
            if (pm.isMaxRuntimeEnabled())
                p_runtime.setVisible(true);
            else
                p_runtime.setVisible(false);
        }
    }

    @Override
    protected PreferenceFragment newConcreteInstance(String rootKey) {
        return PreferenceFragment.newInstance(getArguments().getInt(ARG_PREFERENCES_RES_ID), getArguments().getInt(ARG_CONTAINER_RES_ID), rootKey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PreferenceManager.IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            //For API < 23 we ignore battery optimization
            boolean isIgnoringBatteryOptimizations = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
            }
            if (isIgnoringBatteryOptimizations) {
                // Ignoring battery optimization
                ServiceUtil.scheduleJob(getActivity());
            } else {
                // Not ignoring battery optimization
                disableScheduler();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        PreferenceScreen ourPreferenceScreen = findPreference(getString(R.string.Settings_Websites_Categories_Label));
        if (getPreferenceScreen().equals(ourPreferenceScreen)) {
            inflater.inflate(R.menu.website_categories, menu);
        }
        int enabledCategories = ((Application) getActivity().getApplication()).getPreferenceManager().countEnabledCategory();

        if (enabledCategories>=COUNT_WEBSITE_CATEGORIES){
            MenuItem item = menu.findItem(R.id.selectAll);
            if (item != null) {
                item.setVisible(false);
            }
        } else if (enabledCategories<=0){
            MenuItem item = menu.findItem(R.id.selectNone);
            if (item != null) {
                item.setVisible(false);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PreferenceManager preferenceManager = ((Application) getActivity().getApplication()).getPreferenceManager();
        int itemId = item.getItemId();
        if (itemId == R.id.selectAll) {
            preferenceManager.updateAllWebsiteCategories(true);
            getActivity().finish();
            return true;
        } else if (itemId == R.id.selectNone) {
            preferenceManager.updateAllWebsiteCategories(false);
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
