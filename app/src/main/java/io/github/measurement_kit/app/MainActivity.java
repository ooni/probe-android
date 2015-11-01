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

import io.github.measurement_kit.common.Logger;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("measurement-kit-jni-0.0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button;

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
        Logger.setVerbose(1);
        Logger.useAndroidLogger();
        Log.v(TAG, "set log verbose... done");

        Log.v(TAG, "bind dns-injection button...");
        button = (Button)findViewById(R.id.dns_injection_button);
        button.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Log.v(TAG, "clicked-dns-injection");
                    Intent intent = new Intent(MainActivity.this, RunnerService.class);
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
                    Intent intent = new Intent(MainActivity.this, RunnerService.class);
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
                    Intent intent = new Intent(MainActivity.this, RunnerService.class);
                    intent.setAction(OONITests.TCP_CONNECT);
                    MainActivity.this.startService(intent);
                }
            }
        );
        Log.v(TAG, "bind tcp-connect button... done");
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

    private static final String TAG = "main-activity";
}
