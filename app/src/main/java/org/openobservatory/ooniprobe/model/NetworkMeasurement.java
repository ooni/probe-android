package org.openobservatory.ooniprobe.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;

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

    public NetworkMeasurement(String name) {
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-"+ test_id +".log";
        this.json_file = "/test-"+ test_id +".json";
        this.running = true;
        this.viewed = false;
        this.anomaly = 0;
    }

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
            case OONITests.NDT_TEST:
                return context.getString(R.string.ndt);
            case OONITests.DASH:
                return context.getString(R.string.dash);
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
            case OONITests.DASH:
                return context.getString(R.string.dash_desc);
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
            case OONITests.DASH:
                if (anomaly == 0)
                    return R.drawable.dash;
                else if (anomaly == 1)
                    return R.drawable.dash_warning;
                else
                    return R.drawable.dash_no;
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
            case OONITests.NDT_TEST:
                return R.drawable.ndt_big;
            case OONITests.DASH:
                return R.drawable.dash_big;
            default:
                return 0;
        }
    }

}
