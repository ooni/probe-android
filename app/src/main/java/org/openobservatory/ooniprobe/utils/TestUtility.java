package org.openobservatory.ooniprobe.utils;

import android.content.Context;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.ooniprobe.R;

public class TestUtility {
    public static final String WEBSITES = "websites";
    public static final String INSTANT_MESSAGING = "instant_messaging";
    public static final String MIDDLEBOXES = "middleboxes";
    public static final String PERFORMANCE = "performance";

    public static final String DASH = "dash";
    public static final String HTTP_INVALID_REQUEST_LINE = "http_invalid_request_line";
    public static final String HTTP_HEADER_FIELD_MANIPULATION = "http_header_field_manipulation";
    public static final String WEB_CONNECTIVITY = "web_connectivity";
    public static final String NDT = "ndt";
    public static final String NDT_TEST = "ndt_test";
    public static final String WHATSAPP = "whatsapp";
    public static final String TELEGRAM = "telegram";
    public static final String FACEBOOK_MESSENGER = "facebook_messenger";
    public static final String COLLECTOR_ADDRESS = "https://b.collector.ooni.io";
    public static final String MAX_RUNTIME = "90";
    public static final String NOTIFICATION_SERVER_DEV = "https://registry.proteus.test.ooni.io";
    public static final String NOTIFICATION_SERVER_PROD = "https://registry.proteus.ooni.io";
    public static final String NOTIFICATION_SERVER = NOTIFICATION_SERVER_PROD;
    public static final int MK_VERBOSITY = LogSeverity.LOG_INFO;
    public static final int ANOMALY_GREEN = 0;
    public static final int ANOMALY_ORANGE = 1;
    public static final int ANOMALY_RED = 2;

    public static String getTestName(Context context, String name) {
        switch (name) {
            case WEBSITES:
                return context.getString(R.string.Test_Websites_Fullname);
            case INSTANT_MESSAGING:
                return context.getString(R.string.Test_InstantMessaging_Fullname);
            case MIDDLEBOXES:
                return context.getString(R.string.Test_Middleboxes_Fullname);
            case PERFORMANCE:
                return context.getString(R.string.Test_Performance_Fullname);
            case WEB_CONNECTIVITY:
                return context.getString(R.string.Test_WebConnectivity_Fullname);
            case HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.Test_HTTPInvalidRequestLine_Fullname);
            case HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.Test_HTTPHeaderFieldManipulation_Fullname);
            case NDT:
                return context.getString(R.string.Test_NDT_Fullname);
            case DASH:
                return context.getString(R.string.Test_Dash_Fullname);
            case WHATSAPP:
                return context.getString(R.string.Test_WhatsApp_Fullname);
            case TELEGRAM:
                return context.getString(R.string.Test_Telegram_Fullname);
            case FACEBOOK_MESSENGER:
                return context.getString(R.string.Test_FacebookMessenger_Fullname);
            default:
                return "";
        }
    }

    public static String getTestDescr(Context context, String name) {
        switch (name) {
            case WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity_desc);
            case HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line_desc);
            case HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation_desc);
            case NDT_TEST:
                return context.getString(R.string.ndt_desc);
            case NDT:
                return context.getString(R.string.ndt_desc);
            case DASH:
                return context.getString(R.string.dash_desc);
            case WHATSAPP:
                return context.getString(R.string.whatsapp_desc);
            case TELEGRAM:
                return context.getString(R.string.telegram_desc);
            case FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger_desc);
            default:
                return "";
        }
    }

    public static int getTestImage(String name) {
        switch (name) {
            case WEB_CONNECTIVITY:
                    return R.drawable.test_websites;
            case MIDDLEBOXES:
                return R.drawable.test_middle_boxes;
            case INSTANT_MESSAGING:
                return R.drawable.test_instant_messaging;
            case PERFORMANCE:
                return R.drawable.test_performance;
            case WHATSAPP:
                return R.drawable.test_whatsapp;
            case TELEGRAM:
                return R.drawable.test_telegram;
            case FACEBOOK_MESSENGER:
                return R.drawable.test_facebook_messenger;
            default:
                return 0;
        }
    }

    public static String getTestUrl(String name){
        switch (name) {
            case HTTP_INVALID_REQUEST_LINE:
                return "https://ooni.torproject.org/nettest/http-invalid-request-line/";
            case NDT:
                return "https://ooni.torproject.org/nettest/ndt/";
            case WEB_CONNECTIVITY:
                return "https://ooni.torproject.org/nettest/web-connectivity/";
            case HTTP_HEADER_FIELD_MANIPULATION:
                return "https://ooni.torproject.org/nettest/http-header-field-manipulation/";
            case DASH:
                return "https://ooni.torproject.org/nettest/dash/";
            case WHATSAPP:
                return "https://ooni.torproject.org/nettest/whatsapp/";
            case TELEGRAM:
                return "https://ooni.torproject.org/nettest/telegram/";
            case FACEBOOK_MESSENGER:
                return "https://ooni.torproject.org/nettest/facebook-messenger/";
            default:
                return "";
        }
    }

}
