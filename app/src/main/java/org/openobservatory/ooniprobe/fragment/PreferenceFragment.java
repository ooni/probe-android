package org.openobservatory.ooniprobe.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.XmlRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
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

import java.util.Arrays;

import localhost.toolkit.app.fragment.MessageDialogFragment;
import localhost.toolkit.preference.ExtendedPreferenceFragment;

public class PreferenceFragment extends ExtendedPreferenceFragment<PreferenceFragment> implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ARG_PREFERENCES_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.PREF_RES_ID";
    private static final String ARG_CONTAINER_RES_ID = "org.openobservatory.ooniprobe.fragment.PreferenceFragment.CONTAINER_VIEW_ID";
    public static final int IGNORE_OPTIMIZATION_REQUEST = 15;
    private String rootKey;

    public static PreferenceFragment newInstance(@XmlRes int preferencesResId, @IdRes int preferencesContainerResId, String rootKey) {
        PreferenceFragment fragment = new PreferenceFragment();
        fragment.setArguments(new Bundle());
        fragment.getArguments().putInt(ARG_PREFERENCES_RES_ID, preferencesResId);
        fragment.getArguments().putInt(ARG_CONTAINER_RES_ID, preferencesContainerResId);
        fragment.getArguments().putString(ARG_PREFERENCE_ROOT, rootKey);

        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.rootKey = rootKey;
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
                PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
                if(!isIgnoringBatteryOptimizations){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, IGNORE_OPTIMIZATION_REQUEST);
                }
                else
                    ServiceUtil.scheduleJob(getContext());
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

        if (key.equals(getString(R.string.proxy_enable_custom)) ||
                key.equals(getString(R.string.proxy_enable_psiphon))){
            PreferenceManager pm = ((Application) getActivity().getApplication()).getPreferenceManager();
            if (key.equals(getString(R.string.proxy_enable_psiphon)) &&
                    pm.isEnableProxyPsiphon()){
                SwitchPreferenceCompat p = findPreference(getString(R.string.proxy_enable_custom));
                if (p != null)
                    p.setChecked(false);

            }
            else if (key.equals(getString(R.string.proxy_enable_custom)) &&
                    pm.isEnableProxyCustom()){
                SwitchPreferenceCompat p = findPreference(getString(R.string.proxy_enable_psiphon));
                if (p != null)
                    p.setChecked(false);
            }
        }
        else if (preference instanceof SwitchPreferenceCompat) {
            //Call this code only in case of category or tests
            if (Arrays.asList(getActivity().getResources().getStringArray(R.array.CategoryCodes)).contains(key) ||
                    Arrays.asList(getActivity().getResources().getStringArray(R.array.preferenceTestsNames)).contains(key))
                checkAtLeastOneEnabled(sharedPreferences, key);
        }

        if (key.equals(getString(R.string.theme_enabled))) {
            Toast.makeText(getActivity(), "Please restart the app for apply changes.", Toast.LENGTH_LONG).show();
            getActivity().finishAffinity();
        }
        hidePreferences();
    }

    private void checkAtLeastOneEnabled(SharedPreferences sharedPreferences, String key){
        boolean found = false;
        //cycle all preferences in the page and return true if at least one is enabled
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
            if (getPreferenceScreen().getPreference(i) instanceof SwitchPreferenceCompat)
                found = found || sharedPreferences.getBoolean(getPreferenceScreen().getPreference(i).getKey(), true);
        if (!found) {
            new MessageDialogFragment.Builder()
                    .withMessage(getString(R.string.Modal_EnableAtLeastOneTest))
                    .build().show(getChildFragmentManager(), null);
            sharedPreferences.edit().remove(key).apply();
            SwitchPreferenceCompat p = findPreference(key);
            if (p != null)
                p.setChecked(true);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getActivity().getPackageName());
            if (isIgnoringBatteryOptimizations) {
                // Ignoring battery optimization
                ServiceUtil.scheduleJob(getActivity());
            } else {
                // Not ignoring battery optimization
                disableScheduler();
            }
        }
    }
}
