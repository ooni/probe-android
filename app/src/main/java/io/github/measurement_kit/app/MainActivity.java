// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.github.measurement_kit.jni.DnsApi;
import io.github.measurement_kit.jni.LoggerApi;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    ProgressDialog progress;
    Button buttons[] = new Button[5];
    int selected;

    static {
        System.loadLibrary("measurement_kit_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;
        //TODO use Calligraphy https://github.com/chrisjenx/Calligraphy
        TextView tv = (TextView)findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
        tv.setTypeface(font);

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
        Log.v(TAG, "create test-complete receiver... done");

        copyResources();

        Log.v(TAG, "set log verbose...");
        //LoggerApi.setVerbose(1);
        LoggerApi.useAndroidLogger();
        Log.v(TAG, "set log verbose... done");


        Log.v(TAG, "bind tcp-connect button...");
        button = (Button) findViewById(R.id.tcp_connect_button);
        button.setTypeface(font);
        button.setOnClickListener(this);
        buttons[0] = button;
        Log.v(TAG, "bind tcp-connect button... done");

        Log.v(TAG, "bind dns-injection button...");
        button = (Button) findViewById(R.id.dns_injection_button);
        button.setTypeface(font);
        button.setOnClickListener(this);
        buttons[1] = button;
        Log.v(TAG, "bind dns-injection button... done");

        Log.v(TAG, "bind http-invalid-request-line button...");
        button = (Button) findViewById(R.id.http_invalid_request_line_button);
        button.setTypeface(font);
        button.setOnClickListener(this);
        buttons[2] = button;
        Log.v(TAG, "bind http-invalid-request-line button... done");

        Log.v(TAG, "bind check-port button...");
        button = (Button) findViewById(R.id.check_port_button);
        button.setTypeface(font);
        button.setOnClickListener(this);
        buttons[3] = button;
        Log.v(TAG, "bind check-port button... done");

        Log.v(TAG, "bind traceroute button...");
        button = (Button) findViewById(R.id.traceroute_button);
        button.setTypeface(font);
        button.setOnClickListener(this);
        buttons[4] = button;
        Log.v(TAG, "bind traceroute button... done");

        Log.v(TAG, "bind run button...");
        ImageButton run_button = (ImageButton) findViewById(R.id.run_test_button);
        run_button.setOnClickListener(this);
        Log.v(TAG, "bind run button... done");

        Log.v(TAG, "bind log button...");
        button = (Button) findViewById(R.id.log_button);
        button.setTypeface(font);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.v(TAG, "clicked-log");
                        alertScrollView();
                    }
                }
        );
        Log.v(TAG, "bind log button... done");
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
                Log.v(TAG, "clicked-tcp-connect");
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running tcp-connect test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.TCP_CONNECT);
                MainActivity.this.startService(intent);
                break;
            case R.id.dns_injection_button:
                Log.v(TAG, "clicked-dns-injection");
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running dns-injection test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.DNS_INJECTION);
                MainActivity.this.startService(intent);
                break;
            case R.id.http_invalid_request_line_button:
                Log.v(TAG, "clicked-http-invalid-request-line");
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running http-invalid-request-line test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(OONITests.HTTP_INVALID_REQUEST_LINE);
                MainActivity.this.startService(intent);
                break;
            case R.id.check_port_button:
                Log.v(TAG, "clicked-check-port");
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running check-port test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(PortolanTests.CHECK_PORT);
                MainActivity.this.startService(intent);
                break;
            case R.id.traceroute_button:
                Log.v(TAG, "clicked-traceroute");
                progress = ProgressDialog.show(MainActivity.this, "Testing", "running traceroute test", false);
                intent = new Intent(MainActivity.this, SyncRunnerService.class);
                intent.setAction(PortolanTests.TRACEROUTE);
                MainActivity.this.startService(intent);
                break;

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


    public void alertScrollView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

        TextView tv = (TextView) myScrollView
                .findViewById(R.id.textViewWithScroll);

        tv.setText("");
        tv.append(readLogFile());

        new AlertDialog.Builder(MainActivity.this).setView(myScrollView)
                .setTitle("Log View")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }

    public String readLogFile() {
        String logPath = getFilesDir() + "/last-logs.txt";
        File file = new File(getFilesDir(),"/last-logs.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //Need to add proper error handling here
        }
        return text.toString();
    }

    private static final String TAG = "main-activity";

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
}

