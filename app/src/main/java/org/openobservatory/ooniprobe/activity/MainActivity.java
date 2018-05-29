// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.openobservatory.ooniprobe.data.TestStorage;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import org.openobservatory.ooniprobe.R;

public class MainActivity extends AppCompatActivity  {
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkResources();
        TestStorage.oldTestsDetected(this);
        /*
        if (TestStorage.loadTests(this).size() > 0) {
            TestStorage.removeAllTests(this, this);
        }
*/
        checkInformedConsent();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void checkInformedConsent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_run", true)) {
            startInformedConsentActivity();
        }
    }

    public void checkResources() {
        //TODO save version number and update only if needed
        copyResources(R.raw.geoipasnum, "GeoIPASNum.dat");
        copyResources(R.raw.geoip, "GeoIP.dat");
    }

    private void copyResources(int id, String filename) {
        boolean exists = false;
        try {
            openFileInput(filename);
            exists = true;
        } catch (FileNotFoundException exc) {
            /* FALLTHROUGH */
        }
        if (exists) {
            return;
        }
        Log.v(TAG, "copyResources: " + filename + " ...");
        try {
            InputStream in = getResources().openRawResource(id);
            FileOutputStream out = openFileOutput(filename, 0);
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) out.write(buff, 0, read);
        } catch (java.io.IOException err) {
            // XXX suppress exception
            // XXX not closing in and out
            Log.e(TAG, "copyResources: error: " + err + " for: " + filename);
        }
        Log.v(TAG, "copyResources: " + filename + " ... done");
    }

    public void startInformedConsentActivity() {
        Intent InformedConsentIntent = new Intent(MainActivity.this, InformedConsentActivity.class);
        startActivityForResult(InformedConsentIntent, InformedConsentActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InformedConsentActivity.REQUEST_CODE){
            if (resultCode != InformedConsentActivity.RESULT_CODE_COMPLETED) {
                finish();
            }
            else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
            }
        }
    }

    private static final String TAG = "main-activity";
}
