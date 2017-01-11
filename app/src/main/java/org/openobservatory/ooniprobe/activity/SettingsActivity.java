package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.SwitchCompat;

import org.openobservatory.ooniprobe.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity  {
    SharedPreferences preferences;
    RelativeLayout collector_addressLayout;
    public static final String DEFAULT_COLLECTOR = "https://a.collector.test.ooni.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat include_ipButton = (SwitchCompat) findViewById(R.id.ck_include_ip);
        include_ipButton.setChecked(preferences.getBoolean("include_ip", false));
        include_ipButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean("include_ip", true);
                } else {
                    editor.putBoolean("include_ip", false);
                }
                editor.commit();
            }
        });

        SwitchCompat include_asnButton = (SwitchCompat) findViewById(R.id.ck_include_asn);
        include_asnButton.setChecked(preferences.getBoolean("include_asn", true));
        include_asnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean("include_asn", true);
                } else {
                    editor.putBoolean("include_asn", false);
                }
                editor.commit();
            }
        });

        SwitchCompat include_ccButton = (SwitchCompat) findViewById(R.id.ck_include_country);
        include_ccButton.setChecked(preferences.getBoolean("include_cc", true));
        include_ccButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean("include_cc", true);
                } else {
                    editor.putBoolean("include_cc", false);
                }
                editor.commit();
            }
        });

        TextView collector_address = (TextView) findViewById(R.id.collector_address_subText);
        collector_address.setText(preferences.getString("collector_address", DEFAULT_COLLECTOR));

        collector_addressLayout = (RelativeLayout) findViewById(R.id.collector_addressLayout);
        collector_addressLayout.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        if (preferences.getBoolean("upload_results", true))
            collector_addressLayout.setVisibility(View.VISIBLE);
        else
            collector_addressLayout.setVisibility(View.GONE);

        SwitchCompat upload_resultsButton = (SwitchCompat) findViewById(R.id.ck_upload_results);
        upload_resultsButton.setChecked(preferences.getBoolean("upload_results", true));
        upload_resultsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    collector_addressLayout.setVisibility(View.VISIBLE);
                    editor.putBoolean("upload_results", true);
                } else {
                    collector_addressLayout.setVisibility(View.GONE);
                    editor.putBoolean("upload_results", false);
                }
                editor.commit();
            }
        });

        TextView max_runtime = (TextView) findViewById(R.id.max_runtimeEditText);
        max_runtime.setText(preferences.getString("max_runtime", "90"));
        max_runtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("max_runtime", s.toString());
                editor.commit();
            }
        });

    }

    private void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.collector_address));

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(preferences.getString("collector_address", DEFAULT_COLLECTOR));
        builder.setView(input);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("collector_address", input.getText().toString());
                editor.commit();
                //Workaround to reload settings
                TextView collector_address = (TextView) findViewById(R.id.collector_address_subText);
                collector_address.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog d = builder.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private static final String TAG = "settings-activity";
}