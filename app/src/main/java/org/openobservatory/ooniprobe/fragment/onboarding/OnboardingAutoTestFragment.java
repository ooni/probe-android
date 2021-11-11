package org.openobservatory.ooniprobe.fragment.onboarding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class OnboardingAutoTestFragment extends Fragment implements ConfirmDialogFragment.OnConfirmedListener {
    @Inject PreferenceManager preferenceManager;
    public static final String BATTERY_DIALOG = "battery_optimization";

    @Nullable
    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_onboarding_autotest, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.master) void masterClick() {
        enableAutoTest();
    }

    @OnClick(R.id.slave) void slaveClick() {
        next();
    }

    private void enableAutoTest(){
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
            //Enable but don't start.
            preferenceManager.enableAutomatedTesting();
            next();
        }
    }

    public void next(){
        if (ThirdPartyServices.shouldShowOnboardingCrash())
            getParentFragmentManager().beginTransaction().replace(android.R.id.content, new OnboardingCrashFragment()).commit();
        else
            getParentFragmentManager().beginTransaction().replace(android.R.id.content, new Onboarding3Fragment()).commit();
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
                //Enable but don't start.
                preferenceManager.enableAutomatedTesting();
                next();
            }
            else {
                new ConfirmDialogFragment.Builder()
                        .withMessage(getString(R.string.Modal_Autorun_BatteryOptimization))
                        .withPositiveButton(getString(R.string.Modal_OK))
                        .withNegativeButton(getString(R.string.Modal_Cancel))
                        .withExtra(BATTERY_DIALOG)
                        .build().show(getChildFragmentManager(), null);
            }
        }
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (serializable == null) return;
        if (serializable.equals(BATTERY_DIALOG)) {
            //TODO 'void org.openobservatory.ooniprobe.common.PreferenceManager.setNotificationsFromDialog(boolean)' on a null object reference
            preferenceManager.setNotificationsFromDialog(i == DialogInterface.BUTTON_POSITIVE);
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, PreferenceManager.IGNORE_OPTIMIZATION_REQUEST);
            }
        }
    }
}