package org.openobservatory.ooniprobe.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.openobservatory.ooniprobe.R;

public class NetworkMeasurement {
    public final String testName;
    public boolean completed = false;
    public final long test_id;
    public int progress = 0;

    public final String json_file;
    public final String log_file;
    public final Boolean running;
    public final Boolean viewed;

    public NetworkMeasurement(String name){
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-"+ test_id +".log";
        this.json_file = "/test-"+ test_id +".json";
        this.running = false;
        this.viewed = false;
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

    public static int getTestImage(String name, Boolean success) {
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                if (success)
                    return R.drawable.http_invalid_request_line;
                else
                    return R.drawable.http_invalid_request_line_no;
            case OONITests.NDT_TEST:
                if (success)
                    return R.drawable.ndt_test;
                else
                    return R.drawable.ndt_test_no;
            case OONITests.WEB_CONNECTIVITY:
                if (success)
                    return R.drawable.web_connectivity;
                else
                    return R.drawable.web_connectivity_no;
            default:
                return 0;
        }
    }

}
