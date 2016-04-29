// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.netprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.openobservatory.netprobe.adapter.TestsListAdapter;
import org.openobservatory.netprobe.data.TestData;
import org.openobservatory.netprobe.model.NetworkMeasurement;
import org.openobservatory.netprobe.model.OONITests;
import org.openobservatory.netprobe.model.PortolanTests;
import org.openobservatory.measurement_kit.jni.DnsApi;
import org.openobservatory.measurement_kit.jni.LoggerApi;
import org.openobservatory.netprobe.utils.Alert;
import org.openobservatory.netprobe.view.NotScrollableListView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import org.openobservatory.netprobe.R;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener, Observer {

    Button buttons[] = new Button[5];
    int selected;
    private NotScrollableListView mTestsListView;
    private TestsListAdapter mTestsListAdapter;

    static {
        System.loadLibrary("measurement_kit_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestsListView = (NotScrollableListView) findViewById(R.id.listView);
        mTestsListAdapter = new TestsListAdapter(this,  new ArrayList<NetworkMeasurement>());
        mTestsListView.setAdapter(mTestsListAdapter);
        mTestsListView.setLayoutManager(new LinearLayoutManager(this));

        TestData.getInstance().addObserver(this);

        Button button;

        // The app now tries to get DNS from the device. Upon fail, it uses
        // Google DNS resolvers
        DnsApi.clearNameServers();
        ArrayList<String> nameservers = getDNS();
        if (!nameservers.isEmpty()) {
            for (String s : getDNS()) {
                Log.v(TAG, "Adding nameserver: " + s);
                DnsApi.addNameServer(s);
            }
        } else {
            Log.v(TAG, "Could not get DNS from device, using defaults");
            DnsApi.addNameServer("8.8.8.8");
            DnsApi.addNameServer("8.8.4.4");
        }

        /*
        InsideCompleteReceiver receiver = new InsideCompleteReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, new IntentFilter(OONITests.DNS_INJECTION)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, new IntentFilter(OONITests.HTTP_INVALID_REQUEST_LINE)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, new IntentFilter(OONITests.TCP_CONNECT)
        );
    */
        copyResources();

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

        button = (Button) findViewById(R.id.check_port_button);
        button.setOnClickListener(this);
        buttons[3] = button;

        button = (Button) findViewById(R.id.traceroute_button);
        button.setOnClickListener(this);
        buttons[4] = button;

        ImageButton info_button;
        info_button = (ImageButton) findViewById(R.id.tcp_connect_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "ts-008-tcpconnect");
                    }
                }
        );

        info_button = (ImageButton) findViewById(R.id.dns_injection_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "ts-012-dns-injection");
                    }
                }
        );

        info_button = (ImageButton) findViewById(R.id.http_invalid_request_line_info_button);
        info_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertWebView(MainActivity.this, "ts-007-http-invalid-request-line");
                    }
                }
        );


        ImageButton run_button = (ImageButton) findViewById(R.id.run_test_button);
        run_button.setOnClickListener(this);

        /*
        button = (Button) findViewById(R.id.log_button);
        button.setTypeface(font);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Alert.alertScrollView(MainActivity.this);
                    }
                }
        );
        */
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected, 0, 0, 0);
                break;
        }
        selected = v.getId();
    }

    private void executeTest(int test){
        Intent intent;
        switch (test) {
            case R.id.tcp_connect_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.TCP_CONNECT);
                /*
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running tcp-connect test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.TCP_CONNECT);
                MainActivity.this.startService(intent);
                */
                break;
            case R.id.dns_injection_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.DNS_INJECTION);
                /*
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running dns-injection test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.DNS_INJECTION);
                MainActivity.this.startService(intent);
                */
                break;
            case R.id.http_invalid_request_line_button:
                TestData.doNetworkMeasurements(MainActivity.this, OONITests.HTTP_INVALID_REQUEST_LINE);
                /*
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running http-invalid-request-line test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.HTTP_INVALID_REQUEST_LINE);
                MainActivity.this.startService(intent);
                */
                break;
            case R.id.check_port_button:
                TestData.doNetworkMeasurements(MainActivity.this, PortolanTests.CHECK_PORT);
                /*
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(PortolanTests.CHECK_PORT);
                MainActivity.this.startService(intent);
                */
                break;
            case R.id.traceroute_button:
                TestData.doNetworkMeasurements(MainActivity.this, PortolanTests.TRACEROUTE);
                /*
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(PortolanTests.TRACEROUTE);
                MainActivity.this.startService(intent);
                */
                break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mTestsListAdapter != null) {
            mTestsListAdapter.setData(TestData.getInstance().mNetworkMeasurementsRunning);
            mTestsListAdapter.addData(TestData.getInstance().mNetworkMeasurementsFinished);
        }
    }

    private void deselectButtons(){
        for( Button b : buttons ) {
            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_selected, 0, 0, 0);
        }
    }

    private void copyResources() {
        Log.v(TAG, "copyResources...");
        try {
            InputStream in = getResources().openRawResource(R.raw.hosts);
            FileOutputStream out = openFileOutput("hosts.txt", 0);
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

    private ArrayList<String> getDNS() {
        ArrayList<String> servers = new ArrayList<String>();
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);

            for (String name : new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4",}) {
                String value = (String) method.invoke(null, name);
                if (value != null && !value.equals("") && !servers.contains(value)) {
                    servers.add(value);
                }
            }
        // Using 4 branches to show which errors may occur
        // We can just catch Exception
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "getDNS: error: " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getDNS: error: " + e);
        }
        return servers;
    }

    private static final String TAG = "main-activity";

    /*
    public class InsideCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String testName = intent.getAction();
            Log.v(TAG, "received complete: " + testName);
            // TODO: it's not clear to me how to proceed from here; specifically whether it's
            // safe to call the activity from here, or whether we should cache what we have and
            // wait for the activity to poll us.
            runOnUiThread(new Runnable() {
                public void run() {
                    progress.dismiss();
                    alertScrollView();
                }
            });
        }

        private static final String TAG = "test-complete-receiver";
    }
    */
}

