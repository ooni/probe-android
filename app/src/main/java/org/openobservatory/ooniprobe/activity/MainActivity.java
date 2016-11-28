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
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;

import org.openobservatory.ooniprobe.adapter.TestsAvailableListAdapter;
import org.openobservatory.ooniprobe.adapter.TestsListAdapter;
import org.openobservatory.ooniprobe.adapter.TestsRunningListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import org.openobservatory.ooniprobe.model.PortolanTests;
import org.openobservatory.measurement_kit.LoggerApi;
import org.openobservatory.ooniprobe.utils.Alert;
import org.openobservatory.ooniprobe.view.NotScrollableListView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import org.openobservatory.ooniprobe.R;

public class MainActivity extends AppCompatActivity implements Observer {

    private NotScrollableListView mAvailableTestsListView;
    private NotScrollableListView mRunningTestsListView;
    private NotScrollableListView mFinishedTestsListView;
    private TestsAvailableListAdapter mAvailableTestsListAdapter;
    private TestsRunningListAdapter mRunningTestsListAdapter;
    private TestsListAdapter mFinishedTestsListAdapter;
    private static TestStorage ts;

    static {
        System.loadLibrary("measurement_kit");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkResources();

        ts = new TestStorage();
        TestData.getInstance(this).addObserver(this);

        mAvailableTestsListView = (NotScrollableListView) findViewById(R.id.availableTests);
        mAvailableTestsListAdapter = new TestsAvailableListAdapter(this, TestData.getInstance(this).availableTests);
        mAvailableTestsListView.setAdapter(mAvailableTestsListAdapter);
        mAvailableTestsListView.setLayoutManager(new LinearLayoutManager(this));
        mAvailableTestsListAdapter.setData(TestData.getInstance(this).availableTests);

        mRunningTestsListView = (NotScrollableListView) findViewById(R.id.runningTests);
        mRunningTestsListAdapter = new TestsRunningListAdapter(this, new ArrayList<NetworkMeasurement>());
        mRunningTestsListView.setAdapter(mRunningTestsListAdapter);
        mRunningTestsListView.setLayoutManager(new LinearLayoutManager(this));
        //mRunningTestsListAdapter.setData(new ArrayList());

        mFinishedTestsListView = (NotScrollableListView) findViewById(R.id.finishedTests);
        mFinishedTestsListAdapter = new TestsListAdapter(this, new ArrayList<NetworkMeasurement>());
        mFinishedTestsListView.setAdapter(mFinishedTestsListAdapter);
        mFinishedTestsListView.setLayoutManager(new LinearLayoutManager(this));
        mFinishedTestsListAdapter.setData(ts.loadTestsReverse(this));

        //LoggerApi.setVerbose(1);
        LoggerApi.useAndroidLogger();

        checkInformedConsent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mFinishedTestsListAdapter != null) {
            ArrayList<NetworkMeasurement> finishedTests = new ArrayList<NetworkMeasurement>(TestData.getInstance(this).finishedTests);
            Collections.reverse(finishedTests);
            mFinishedTestsListAdapter.setData(finishedTests);
        }
        if (mRunningTestsListAdapter != null) {
            mRunningTestsListAdapter.setData(TestData.getInstance(this).runningTests);
        }
        if (mAvailableTestsListAdapter != null) {
            mAvailableTestsListAdapter.setData(TestData.getInstance(this).availableTests);
        }
        System.out.println("update "+ observable);
    }

    public void checkResources() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("resources_copied", false)) {
            copyResources(R.raw.hosts, "hosts.txt");
            copyResources(R.raw.geoip, "GeoIPASNum.dat");
            copyResources(R.raw.geoipasnum, "GeoIP.dat");
            copyResources(R.raw.cacert, "cacert.pem");
            copyResources(R.raw.urls, "urls.txt");
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("resources_copied", true).apply();
        }
    }

    private void copyResources(int id, String filename) {
        Log.v(TAG, "copyResources...");
        try {
            InputStream in = getResources().openRawResource(id);
            FileOutputStream out = openFileOutput(filename, 0);
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) out.write(buff, 0, read);
        } catch (java.io.IOException err) {
            // XXX suppress exception
            // XXX not closing in and out
            Log.e(TAG, "copyResources: error: " + err);
        }
        Log.v(TAG, "copyResources... done");
    }


    public void checkInformedConsent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_run", true)) {
            startInformedConsentActivity();
        }
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
                showToast(R.string.ooniprobe_configured, true);
            }
        }
    }

    public void showToast(int string, boolean success){
        Toast toast = Toast.makeText(this, string, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(success ? R.drawable.success_toast_bg : R.drawable.error_toast_bg);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);;
        text.setTextColor(getResources().getColor(success ? R.color.successTextColor : R.color.errorTextColor));
        toast.show();
    }

    private static final String TAG = "main-activity";
}

