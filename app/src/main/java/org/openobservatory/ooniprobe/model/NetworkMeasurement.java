package org.openobservatory.ooniprobe.model;

import android.content.Context;

import org.openobservatory.ooniprobe.R;

public class NetworkMeasurement {
    public final String testName;
    public boolean completed = false;
    public final long test_id;
    public int progress = 0;

    public final String json_file;
    public final String log_file;
    //public final String status;

    public NetworkMeasurement(String name){
        this.testName = name;
        this.test_id = System.currentTimeMillis();
        this.log_file = "/test-"+ test_id +".log";
        this.json_file = "/test-"+ test_id +".json";
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
            case PortolanTests.CHECK_PORT:
                return context.getString(R.string.check_port);
            case PortolanTests.TRACEROUTE:
                return context.getString(R.string.traceroute);
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
}
