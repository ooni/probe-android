package org.openobservatory.netprobe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import org.openobservatory.netprobe.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by lorenzo on 17/05/16.
 */
public class SettingsActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private static final String TAG = "settings-activity";
}

/* HOW TO SAVE
//--Init
int myvar = 12;

//--SAVE Data
SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = preferences.edit();
editor.putInt("var1", myvar);
editor.commit();


//--READ data
myvar = preferences.getInt("var1", 0);
 */