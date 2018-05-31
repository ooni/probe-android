package org.openobservatory.ooniprobe.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.measurement_kit.nettests.DashTest;
import org.openobservatory.measurement_kit.nettests.FacebookMessengerTest;
import org.openobservatory.measurement_kit.nettests.HttpHeaderFieldManipulationTest;
import org.openobservatory.measurement_kit.nettests.HttpInvalidRequestLineTest;
import org.openobservatory.measurement_kit.nettests.NdtTest;
import org.openobservatory.measurement_kit.nettests.TelegramTest;
import org.openobservatory.measurement_kit.nettests.WebConnectivityTest;
import org.openobservatory.measurement_kit.nettests.WhatsappTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.utils.TestLists;

import java.util.ArrayList;

public class NetworkMeasurement {
    public String testName = "";
    public boolean entry = false;
    public long test_id = 0;
    public int progress = 0;

    public final String json_file;
    public final String log_file;
    public boolean running = false;
    public boolean viewed = false;
    public int anomaly = 0;
    public transient BaseTest test;

    public NetworkMeasurement(Context context, String name) {
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-" + test_id + ".log";
        this.json_file = "/test-" + test_id + ".json";
        this.running = true;
        this.viewed = false;
        this.anomaly = 0;
        if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0)
            test = new HttpInvalidRequestLineTest();
        else if (testName.compareTo(OONITests.HTTP_HEADER_FIELD_MANIPULATION) == 0)
            test = new HttpHeaderFieldManipulationTest();
        else if (testName.compareTo(OONITests.WEB_CONNECTIVITY) == 0) {
            test = new WebConnectivityTest();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String max_runtime = preferences.getString("max_runtime", OONITests.MAX_RUNTIME);
            test.set_options("max_runtime", max_runtime);
            ArrayList<String> urls = TestLists.getInstance(context).getUrls();
            for (int i = 0; i < urls.size(); i++)
                test.add_input(urls.get(i));
            System.out.println(urls);
        }
        else if (testName.compareTo(OONITests.NDT) == 0)
            test = new NdtTest();
        else if (testName.compareTo(OONITests.DASH) == 0)
            test = new DashTest();
        else if (testName.compareTo(OONITests.WHATSAPP) == 0)
            test = new WhatsappTest();
        else if (testName.compareTo(OONITests.TELEGRAM) == 0)
            test = new TelegramTest();
        else if (testName.compareTo(OONITests.FACEBOOK_MESSENGER) == 0)
            test = new FacebookMessengerTest();
    }

    //Test ran from uri scheme screen
    public NetworkMeasurement(Context context, String name, ArrayList<String>urls) {
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-" + test_id + ".log";
        this.json_file = "/test-" + test_id + ".json";
        this.running = true;
        this.viewed = false;
        this.anomaly = 0;
        if (testName.compareTo(OONITests.HTTP_INVALID_REQUEST_LINE) == 0)
            test = new HttpInvalidRequestLineTest();
        else if (testName.compareTo(OONITests.HTTP_HEADER_FIELD_MANIPULATION) == 0)
            test = new HttpHeaderFieldManipulationTest();
        else if (testName.compareTo(OONITests.WEB_CONNECTIVITY) == 0) {
            test = new WebConnectivityTest();
            for (int i = 0; i < urls.size(); i++)
                test.add_input(urls.get(i));
        }
        else if (testName.compareTo(OONITests.NDT) == 0)
            test = new NdtTest();
        else if (testName.compareTo(OONITests.DASH) == 0)
            test = new DashTest();
        else if (testName.compareTo(OONITests.WHATSAPP) == 0)
            test = new WhatsappTest();
        else if (testName.compareTo(OONITests.TELEGRAM) == 0)
            test = new TelegramTest();
        else if (testName.compareTo(OONITests.FACEBOOK_MESSENGER) == 0)
            test = new FacebookMessengerTest();
    }

    @NonNull
    public static String getTestName(Context context, String name) {
        switch (name) {
            case OONITests.DNS_INJECTION:
                return context.getString(R.string.dns_injection);
            case OONITests.TCP_CONNECT:
                return context.getString(R.string.tcp_connect);
            case OONITests.WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity);
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line);
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation);
            case OONITests.NDT:
                return context.getString(R.string.ndt);
            case OONITests.NDT_TEST:
                return context.getString(R.string.ndt);
            case OONITests.DASH:
                return context.getString(R.string.dash);
            case OONITests.WHATSAPP:
                return context.getString(R.string.whatsapp);
            case OONITests.TELEGRAM:
                return context.getString(R.string.telegram);
            case OONITests.FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger);
            default:
                return "";
        }
    }

    public static String getTestDescr(Context context, String name) {
        switch (name) {
            case OONITests.WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity_desc);
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line_desc);
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation_desc);
            case OONITests.NDT_TEST:
                return context.getString(R.string.ndt_desc);
            case OONITests.NDT:
                return context.getString(R.string.ndt_desc);
            case OONITests.DASH:
                return context.getString(R.string.dash_desc);
            case OONITests.WHATSAPP:
                return context.getString(R.string.whatsapp_desc);
            case OONITests.TELEGRAM:
                return context.getString(R.string.telegram_desc);
            case OONITests.FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger_desc);
            default:
                return "";
        }
    }



}
