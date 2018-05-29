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
                return context.getString(R.string.web_connectivity);
            case HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line);
            case HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation);
            case NDT:
                return context.getString(R.string.ndt);
            case NDT_TEST:
                return context.getString(R.string.ndt);
            case DASH:
                return context.getString(R.string.dash);
            case WHATSAPP:
                return context.getString(R.string.whatsapp);
            case TELEGRAM:
                return context.getString(R.string.telegram);
            case FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger);
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
                if (anomaly == 0)
                    return R.drawable.web_connectivity;
                else if (anomaly == 1)
                    return R.drawable.web_connectivity_warning;
                else
                    return R.drawable.web_connectivity_no;
            case HTTP_INVALID_REQUEST_LINE:
                if (anomaly == 0)
                    return R.drawable.http_invalid_request_line;
                else if (anomaly == 1)
                    return R.drawable.http_invalid_request_line_warning;
                else
                    return R.drawable.http_invalid_request_line_no;
            case HTTP_HEADER_FIELD_MANIPULATION:
                if (anomaly == 0)
                    return R.drawable.http_header_field_manipulation;
                else if (anomaly == 1)
                    return R.drawable.http_header_field_manipulation_warning;
                else
                    return R.drawable.http_header_field_manipulation_no;
            case NDT_TEST:
                if (anomaly == 0)
                    return R.drawable.ndt;
                else if (anomaly == 1)
                    return R.drawable.ndt_warning;
                else
                    return R.drawable.ndt_no;
            case NDT:
                if (anomaly == 0)
                    return R.drawable.ndt;
                else if (anomaly == 1)
                    return R.drawable.ndt_warning;
                else
                    return R.drawable.ndt_no;
            case DASH:
                if (anomaly == 0)
                    return R.drawable.dash;
                else if (anomaly == 1)
                    return R.drawable.dash_warning;
                else
                    return R.drawable.dash_no;
            case WHATSAPP:
                if (anomaly == 0)
                    return R.drawable.whatsapp;
                else if (anomaly == 1)
                    return R.drawable.whatsapp_warning;
                else
                    return R.drawable.whatsapp_no;
            case TELEGRAM:
                if (anomaly == 0)
                    return R.drawable.telegram;
                else if (anomaly == 1)
                    return R.drawable.telegram_warning;
                else
                    return R.drawable.telegram_no;
            case FACEBOOK_MESSENGER:
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
            case WEB_CONNECTIVITY:
                return R.drawable.web_connectivity_big;
            case HTTP_INVALID_REQUEST_LINE:
                return R.drawable.http_invalid_request_line_big;
            case HTTP_HEADER_FIELD_MANIPULATION:
                return R.drawable.http_header_field_manipulation_big;
            case NDT:
                return R.drawable.ndt_big;
            case DASH:
                return R.drawable.dash_big;
            case WHATSAPP:
                return R.drawable.whatsapp_big;
            case TELEGRAM:
                return R.drawable.telegram_big;
            case FACEBOOK_MESSENGER:
                return R.drawable.facebook_messenger_big;
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

    public static String getTestDesc(Context context, String name) {
        switch (name) {
            case WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity_longdesc);
            case HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line_longdesc);
            case HTTP_HEADER_FIELD_MANIPULATION:
                return context.getString(R.string.http_header_field_manipulation_longdesc);
            case NDT:
                return context.getString(R.string.ndt_longdesc);
            case DASH:
                return context.getString(R.string.dash_longdesc);
            case WHATSAPP:
                return context.getString(R.string.whatsapp_longdesc);
            case TELEGRAM:
                return context.getString(R.string.telegram_longdesc);
            case FACEBOOK_MESSENGER:
                return context.getString(R.string.facebook_messenger_longdesc);

            default:
                return "";
        }
    }
}
