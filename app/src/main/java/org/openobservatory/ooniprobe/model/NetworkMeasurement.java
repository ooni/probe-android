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
            ArrayList<String> urls = TestLists.getInstance(context).getUrls();
            setMaxRuntime(context);
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
            if (urls.size() == 0) {
                setMaxRuntime(context);
                urls = TestLists.getInstance(context).getUrls();
            }
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

    public void setMaxRuntime(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String max_runtime = preferences.getString("max_runtime", OONITests.MAX_RUNTIME);
        test.set_options("max_runtime", max_runtime);
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

    public static int getTestImage(String name, int anomaly) {
        switch (name) {
            case OONITests.WEB_CONNECTIVITY:
                if (anomaly == 0)
                    return R.drawable.web_connectivity;
                else if (anomaly == 1)
                    return R.drawable.web_connectivity_warning;
                else
                    return R.drawable.web_connectivity_no;
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                if (anomaly == 0)
                    return R.drawable.http_invalid_request_line;
                else if (anomaly == 1)
                    return R.drawable.http_invalid_request_line_warning;
                else
                    return R.drawable.http_invalid_request_line_no;
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                if (anomaly == 0)
                    return R.drawable.http_header_field_manipulation;
                else if (anomaly == 1)
                    return R.drawable.http_header_field_manipulation_warning;
                else
                    return R.drawable.http_header_field_manipulation_no;
            case OONITests.NDT_TEST:
                if (anomaly == 0)
                    return R.drawable.ndt;
                else if (anomaly == 1)
                    return R.drawable.ndt_warning;
                else
                    return R.drawable.ndt_no;
            case OONITests.NDT:
                if (anomaly == 0)
                    return R.drawable.ndt;
                else if (anomaly == 1)
                    return R.drawable.ndt_warning;
                else
                    return R.drawable.ndt_no;
            case OONITests.DASH:
                if (anomaly == 0)
                    return R.drawable.dash;
                else if (anomaly == 1)
                    return R.drawable.dash_warning;
                else
                    return R.drawable.dash_no;
            case OONITests.WHATSAPP:
                if (anomaly == 0)
                    return R.drawable.whatsapp;
                else if (anomaly == 1)
                    return R.drawable.whatsapp_warning;
                else
                    return R.drawable.whatsapp_no;
            case OONITests.TELEGRAM:
                if (anomaly == 0)
                    return R.drawable.telegram;
                else if (anomaly == 1)
                    return R.drawable.telegram_warning;
                else
                    return R.drawable.telegram_no;
            case OONITests.FACEBOOK_MESSENGER:
                if (anomaly == 0)
                    return R.drawable.facebook_messenger;
                else if (anomaly == 1)
                    return R.drawable.facebook_messenger_warning;
                else
                    return R.drawable.facebook_messenger_no;
            default:
                return 0;
        }
    }

    public static int getTestImageBig(String name) {
        switch (name) {
            case OONITests.WEB_CONNECTIVITY:
                return R.drawable.web_connectivity_big;
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return R.drawable.http_invalid_request_line_big;
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return R.drawable.http_header_field_manipulation_big;
            case OONITests.NDT:
                return R.drawable.ndt_big;
            case OONITests.DASH:
                return R.drawable.dash_big;
            case OONITests.WHATSAPP:
                return R.drawable.whatsapp_big;
            case OONITests.TELEGRAM:
                return R.drawable.telegram_big;
            case OONITests.FACEBOOK_MESSENGER:
                return R.drawable.facebook_messenger_big;
            default:
                return 0;
        }
    }

    public static String getTestUrl(String name){
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return "https://ooni.torproject.org/nettest/http-invalid-request-line/";
            case OONITests.NDT:
                return "https://ooni.torproject.org/nettest/ndt/";
            case OONITests.WEB_CONNECTIVITY:
                return "https://ooni.torproject.org/nettest/web-connectivity/";
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return "https://ooni.torproject.org/nettest/http-header-field-manipulation/";
            case OONITests.DASH:
                return "https://ooni.torproject.org/nettest/dash/";
            case OONITests.WHATSAPP:
                return "https://ooni.torproject.org/nettest/whatsapp/";
            case OONITests.TELEGRAM:
                return "https://ooni.torproject.org/nettest/telegram/";
            case OONITests.FACEBOOK_MESSENGER:
                return "https://ooni.torproject.org/nettest/facebook-messenger/";
            default:
                return "";
        }
    }

    public static String getTestDesc(Context context, String name) {
        switch (name) {
            case OONITests.WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity_longdesc);
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line_longdesc);
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation_longdesc);
            case OONITests.NDT:
                return context.getString(R.string.ndt_longdesc);
            case OONITests.DASH:
                return context.getString(R.string.dash_longdesc);
            case OONITests.WHATSAPP:
                return context.getString(R.string.whatsapp_longdesc);
            case OONITests.TELEGRAM:
                return context.getString(R.string.telegram_longdesc);
            case OONITests.FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger_longdesc);

            default:
                return "";
        }
    }
}
