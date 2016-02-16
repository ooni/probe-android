// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.app;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.github.measurement_kit.jni.DnsApi;
import io.github.measurement_kit.jni.LoggerApi;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("measurement_kit_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button;


        // The app now tries to get DNS from the device. Upon fail, it uses
        // Google DNS resolvers


        Log.v(TAG, "Adding nameservers...");
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
        Log.v(TAG, "Adding nameservers... done");

        Log.v(TAG, "create test-complete receiver...");
        TestCompleteReceiver receiver = new TestCompleteReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver, new IntentFilter(OONITests.DNS_INJECTION)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver, new IntentFilter(OONITests.HTTP_INVALID_REQUEST_LINE)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver, new IntentFilter(OONITests.TCP_CONNECT)
        );
        Log.v(TAG, "create test-complete receiver... done");

        copyResources();

        Log.v(TAG, "set log verbose...");
        //LoggerApi.setVerbose(1);
        LoggerApi.useAndroidLogger();
        Log.v(TAG, "set log verbose... done");

        Log.v(TAG, "bind dns-injection button...");
        button = (Button)findViewById(R.id.dns_injection_button);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.v(TAG, "clicked-dns-injection");
                        Intent intent = new Intent(MainActivity.this, SyncRunnerService.class);
                        intent.setAction(OONITests.DNS_INJECTION);
                        MainActivity.this.startService(intent);
                    }
                }
        );
        Log.v(TAG, "bind dns-injection button... done");

        Log.v(TAG, "bind http-invalid-request-line button...");
        button = (Button)findViewById(R.id.http_invalid_request_line_button);
        button.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Log.v(TAG, "clicked-http-invalid-request-line");
                    Intent intent = new Intent(MainActivity.this, SyncRunnerService.class);
                    intent.setAction(OONITests.HTTP_INVALID_REQUEST_LINE);
                    MainActivity.this.startService(intent);
                }
            }
        );
        Log.v(TAG, "bind http-invalid-request-line button... done");

        Log.v(TAG, "bind tcp-connect button...");
        button = (Button)findViewById(R.id.tcp_connect_button);
        button.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Log.v(TAG, "clicked-tcp-connect");
                    Intent intent = new Intent(MainActivity.this, SyncRunnerService.class);
                    intent.setAction(OONITests.TCP_CONNECT);
                    MainActivity.this.startService(intent);
                }
            }
        );
        Log.v(TAG, "bind tcp-connect button... done");

        Log.v(TAG, "bind check-port button...");
        button = (Button)findViewById(R.id.check_port_button);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.v(TAG, "clicked-check-port");
                        Intent intent = new Intent(MainActivity.this, SyncRunnerService.class);
                        intent.setAction(PortolanTests.CHECK_PORT);
                        MainActivity.this.startService(intent);
                    }
                }
        );
        Log.v(TAG, "bind check-port button... done");

        Log.v(TAG, "bind traceroute button...");
        button = (Button)findViewById(R.id.traceroute_button);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.v(TAG, "clicked-traceroute");
                        Intent intent = new Intent(MainActivity.this, SyncRunnerService.class);
                        intent.setAction(PortolanTests.TRACEROUTE);
                        MainActivity.this.startService(intent);
                    }
                }
        );
        Log.v(TAG, "bind traceroute button... done");
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
}
