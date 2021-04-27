package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;

public class ProxyActivity extends AbstractActivity {

    private RadioGroup customProxyRadioGroup;
    private TextInputLayout customProxyHostname;
    private TextInputLayout customProxyPort;
    private TextInputLayout customProxyUsername;
    private TextInputLayout customProxyPassword;
    private PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        RadioButton proxyNoneRB = (RadioButton) findViewById(R.id.proxyNone);
        RadioButton proxyPsiphonRB = (RadioButton) findViewById(R.id.proxyPsiphon);
        RadioButton proxyCustomRB = (RadioButton) findViewById(R.id.proxyCustom);
        RadioButton customProxyHTTP = (RadioButton) findViewById(R.id.customProxyHTTP);
        RadioButton customProxySOCKS5 = (RadioButton) findViewById(R.id.customProxySOCKS5);

        customProxyHostname = (TextInputLayout) findViewById(R.id.customProxyHostname);
        customProxyPort = (TextInputLayout) findViewById(R.id.customProxyPort);
        customProxyUsername = (TextInputLayout) findViewById(R.id.customProxyUsername);
        customProxyPassword = (TextInputLayout) findViewById(R.id.customProxyPassword);
        customProxyRadioGroup = (RadioGroup) findViewById(R.id.customProxyRadioGroup);

        CheckBox psiphonOverCustomChB = (CheckBox) findViewById(R.id.psiphonOverCustom);

        pm = getPreferenceManager();

        if (pm.getProxySelected().equals("none")) {
            proxyNoneRB.setChecked(true);
        }else if (pm.isEnableProxyPsiphon()) {
            proxyPsiphonRB.setChecked(true);
        }else if (pm.isEnableProxyCustom()) {
            proxyCustomRB.setChecked(true);
        }
        customProxySetEnabled(pm.isEnableProxyCustom());

        if (pm.isEnableProxyPsiphonOverCustom()) {
            psiphonOverCustomChB.setChecked(true);
        }
        if (pm.getProxyCustomProtocol().equals("HTTP")) {
            customProxyHTTP.setChecked(true);
        }
        else {
            customProxySOCKS5.setChecked(true);
        }

        customProxyHostname.getEditText().setText(pm.getProxyCustomHostname());
        customProxyPort.getEditText().setText(pm.getProxyCustomPort());
        customProxyUsername.getEditText().setText(pm.getProxyCustomUsername());
        customProxyPassword.getEditText().setText(pm.getProxyCustomPassword());

        RadioGroup proxyRadioGroup = (RadioGroup) findViewById(R.id.proxyRadioGroup);
        proxyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.proxyNone) {
                pm.setProxySelected("none");
                customProxySetEnabled(false);
            } else if (checkedId == R.id.proxyPsiphon) {
                pm.setProxySelected("psiphon");
                customProxySetEnabled(false);
            } else if (checkedId == R.id.proxyCustom) {
                pm.setProxySelected("custom");
                customProxySetEnabled(true);
            }
        });

        customProxyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.customProxyHTTP) {
                pm.setProxyCustomProtocol("HTTP");
            } else if (checkedId == R.id.customProxySOCKS5) {
                pm.setProxyCustomProtocol("SOCKS5");
            }
        });

        customProxyHostname.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
                pm.setProxyCustomHostname(customProxyHostname.getEditText().getText().toString());
            }
        });

        customProxyPort.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
                pm.setProxyCustomPort(customProxyPort.getEditText().getText().toString());
            }
        });

        customProxyUsername.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
                pm.setProxyCustomUsername(customProxyUsername.getEditText().getText().toString());
            }
        });

        customProxyPassword.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
                pm.setProxyCustomPassword(customProxyPassword.getEditText().getText().toString());
            }
        });

        psiphonOverCustomChB.setOnCheckedChangeListener(( CheckBox, isChecked) -> {
            pm.seEnabledProxyPsiphonOverCustom(isChecked);
        });


    }

    private void customProxyTextInputSetEnabled (@NonNull TextInputLayout input, boolean flag) {
        input.getEditText().setEnabled(flag);
        input.getEditText().setFocusable(flag);
        input.getEditText().setFocusableInTouchMode(flag);
    }

    private void customProxySetEnabled (boolean flag) {
        for (int i = 0; i < customProxyRadioGroup.getChildCount(); i++) {
            customProxyRadioGroup.getChildAt(i).setEnabled(flag);
            customProxyTextInputSetEnabled(customProxyHostname,flag);
            customProxyTextInputSetEnabled(customProxyPort,flag);
            customProxyTextInputSetEnabled(customProxyUsername,flag);
            customProxyTextInputSetEnabled(customProxyPassword,flag);
        }
    }

    @Override
    public void onBackPressed() {
        pm.setProxyCustomHostname(customProxyHostname.getEditText().getText().toString());
        pm.setProxyCustomPort(customProxyPort.getEditText().getText().toString());
        pm.setProxyCustomUsername(customProxyUsername.getEditText().getText().toString());
        pm.setProxyCustomPassword(customProxyPassword.getEditText().getText().toString());

        if (pm.getProxySelected().equals("none") || pm.isEnableProxyPsiphon()) {
            ProxyActivity.super.onBackPressed();
        }else if (pm.isEnableProxyCustom()) {
            if (pm.getProxyCustomHostname().isEmpty()) {
                customProxyHostname.clearFocus();
                customProxyHostname.setError("invalid input");
            }else if (pm.getProxyCustomPort().isEmpty()){
                customProxyPort.clearFocus();
                customProxyPort.setError("invalid input");
            }else if (pm.getProxyCustomUsername().isEmpty()
                    && !pm.getProxyCustomPassword().isEmpty()) {
                customProxyUsername.clearFocus();
                customProxyUsername.setError("invalid input");
            }else {
                String customProxyTemp = "";
                if (pm.isEnableProxyPsiphonOverCustom()) {
                    customProxyTemp += "psiphon+";
                }
                if (pm.getProxyCustomProtocol().equals("HTTP")) {
                    customProxyTemp += "http://";
                }else if (pm.getProxyCustomProtocol().equals("SOCKS5")) {
                    customProxyTemp += "socks5://";
                }
                if (!pm.getProxyCustomUsername().isEmpty()) {
                    customProxyTemp += pm.getProxyCustomUsername()
                            + ":"
                            + pm.getProxyCustomPassword() //empty passwords are allowed?!
                            + "@";
                }
                customProxyTemp += pm.getProxyCustomHostname()
                        + ":"
                        + pm.getProxyCustomPort()
                        + "/";
                pm.setProxyURL(customProxyTemp);

                ProxyActivity.super.onBackPressed();
            }
        }
    }

}