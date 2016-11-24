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
import java.util.Observable;
import java.util.Observer;

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

public class MainActivity extends AppCompatActivity implements Button.OnClickListener, Observer {

    Button buttons[] = new Button[7];
    int selected;
    private NotScrollableListView mRunningTestsListView;
    private NotScrollableListView mFinishedTestsListView;
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

        ts = new TestStorage();

        mRunningTestsListView = (NotScrollableListView) findViewById(R.id.runningTests);
        mRunningTestsListAdapter = new TestsRunningListAdapter(this, new ArrayList<NetworkMeasurement>());
        mRunningTestsListView.setAdapter(mRunningTestsListAdapter);
        mRunningTestsListView.setLayoutManager(new LinearLayoutManager(this));
        mRunningTestsListAdapter.setData(new ArrayList());

        mFinishedTestsListView = (NotScrollableListView) findViewById(R.id.finishedTests);
        mFinishedTestsListAdapter = new TestsListAdapter(this, new ArrayList<NetworkMeasurement>());
        mFinishedTestsListView.setAdapter(mFinishedTestsListAdapter);
        mFinishedTestsListView.setLayoutManager(new LinearLayoutManager(this));
        mFinishedTestsListAdapter.setData(ts.loadTestsReverse(this));

        TestData.getInstance(this).addObserver(this);

        Button button;
        copyResources(R.raw.hosts, "hosts.txt");
        copyResources(R.raw.geoip, "GeoIPASNum.dat");
        copyResources(R.raw.geoipasnum, "GeoIP.dat");
        copyResources(R.raw.cacert, "cacert.pem");
        copyResources(R.raw.urls, "urls.txt");

        //LoggerApi.setVerbose(1);
        LoggerApi.useAndroidLogger();

        button = (Button) findViewById(R.id.tcp_connect_button);
        button.setOnClickListener(this);
        buttons[0] = button;

        button = (Button) findViewById(R.id.dns_injection_button);
        button.setOnClickListener(this);
        buttons[1] = button;

        button = (Button) findViewById(R.id.http_invalid_request_line_button);
        button.setOnClickListener(this);
        buttons[2] = button;

        button = (Button) findViewById(R.id.web_connectivity_button);
        button.setOnClickListener(this);
        buttons[3] = button;

        button = (Button) findViewById(R.id.ndt_test_button);
        button.setOnClickListener(this);
        buttons[4] = button;

        button = (Button) findViewById(R.id.check_port_button);
        button.setOnClickListener(this);
        buttons[5] = button;

        button = (Button) findViewById(R.id.traceroute_button);
        button.setOnClickListener(this);
        buttons[6] = button;

        ImageButton info_button;
        info_button = (ImageButton) findViewById(R.id.tcp_connect_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "tcp-connect");
                    }
                }
        );

        info_button = (ImageButton) findViewById(R.id.dns_injection_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "dns-injection");
                    }
                }
        );

        info_button = (ImageButton) findViewById(R.id.http_invalid_request_line_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "http-invalid-request-line");
                    }
                }
        );

        info_button = (ImageButton) findViewById(R.id.web_connectivity_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "web-connectivity");
                    }
                }
        );

        ImageButton run_button = (ImageButton) findViewById(R.id.run_test_button);
        run_button.setOnClickListener(this);

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
    public void onClick(View v) {
        deselectButtons();
        switch (v.getId()) {
            case R.id.run_test_button:
                if (selected != -1){
                    executeTest(selected);
                }
                break;
            default:
                Button b = (Button) findViewById(v.getId());
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_button_on, 0, 0, 0);
                break;
        }
        selected = v.getId();
    }

    private void executeTest(int test){
        Intent intent;
        switch (test) {
            case R.id.tcp_connect_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.TCP_CONNECT);
                break;
            case R.id.dns_injection_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.DNS_INJECTION);
                break;
            case R.id.http_invalid_request_line_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.HTTP_INVALID_REQUEST_LINE);
                break;
            case R.id.web_connectivity_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.WEB_CONNECTIVITY);
                break;
            case R.id.ndt_test_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.NDT_TEST);
                break;
            case R.id.check_port_button:
                TestData.doNetworkMeasurements(MainActivity.this, PortolanTests.CHECK_PORT);
                break;
            case R.id.traceroute_button:
                TestData.doNetworkMeasurements(MainActivity.this, PortolanTests.TRACEROUTE);
                break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mFinishedTestsListAdapter != null) {
            ArrayList finishedTests = TestData.getInstance(this).finishedTests;
            Collections.reverse(finishedTests);
            mFinishedTestsListAdapter.setData(finishedTests);
        }
        if (mRunningTestsListAdapter != null) {
            mRunningTestsListAdapter.setData(TestData.getInstance(this).runningTests);
        }
        System.out.println("update "+ observable);
    }

    private void deselectButtons(){
        for( Button b : buttons ) {
            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_button_off, 0, 0, 0);
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

