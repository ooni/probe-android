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
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line);
            case OONITests.NDT_TEST:
                return context.getString(R.string.ndt_test);
            case OONITests.TCP_CONNECT:
                return context.getString(R.string.tcp_connect);
            case OONITests.WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity);
            default:
                return "";
        }
    }

    public static String getTestDescr(Context context, String name) {
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return context.getString(R.string.http_invalid_request_line_desc);
            case OONITests.NDT_TEST:
                return context.getString(R.string.ndt_test_desc);
            case OONITests.WEB_CONNECTIVITY:
                return context.getString(R.string.web_connectivity_desc);
            default:
                return "";
        }
    }

    public static int getTestImage(String name, int anomaly) {
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                if (anomaly == 0)
                    return R.drawable.http_invalid_request_line;
                else if (anomaly == 1)
                    return R.drawable.http_invalid_request_line_warning;
                else
                    return R.drawable.http_invalid_request_line_no;
            case OONITests.NDT_TEST:
                if (anomaly == 0)
                    return R.drawable.ndt_test;
                else if (anomaly == 1)
                    return R.drawable.ndt_test_warning;
                else
                    return R.drawable.ndt_test_no;
            case OONITests.WEB_CONNECTIVITY:
                if (anomaly == 0)
                    return R.drawable.web_connectivity;
                else if (anomaly == 1)
                    return R.drawable.web_connectivity_warning;
                else
                    return R.drawable.web_connectivity_no;
            default:
                return 0;
        }
    }

    public static int getTestImageBig(String name) {
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return R.drawable.http_invalid_request_line_big;
            case OONITests.NDT_TEST:
                return R.drawable.ndt_test_big;
            case OONITests.WEB_CONNECTIVITY:
                return R.drawable.web_connectivity_big;
            default:
                return 0;
        }
    }

}
