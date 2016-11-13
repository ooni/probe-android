package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.SwitchCompat;

import org.openobservatory.ooniprobe.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by lorenzo on 17/05/16.
 */
public class SettingsActivity extends AppCompatActivity  {
    SharedPreferences preferences;
    RelativeLayout collector_addressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat include_ipButton = (SwitchCompat) findViewById(R.id.include_ipBtn);
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

        SwitchCompat include_asnButton = (SwitchCompat) findViewById(R.id.include_asnBtn);
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

        SwitchCompat include_ccButton = (SwitchCompat) findViewById(R.id.include_ccBtn);
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
        collector_address.setText(preferences.getString("collector_address", "https://a.collector.test.ooni.io"));

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

        SwitchCompat upload_resultsButton = (SwitchCompat) findViewById(R.id.upload_resultsBtn);
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

    }

    private void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.collector_address));

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
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