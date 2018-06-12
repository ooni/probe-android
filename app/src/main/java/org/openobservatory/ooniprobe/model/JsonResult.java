package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

public class JsonResult {

    @SerializedName("probe_asn")
    public String probe_asn;

    @SerializedName("probe_cc")
    public String probe_cc;

    @SerializedName("test_start_time")
    public String test_start_time;

    @SerializedName("measurement_start_time")
    public String measurement_start_time;

    @SerializedName("test_runtime")
    public String test_runtime;

    @SerializedName("probe_ip")
    public String probe_ip;

    @SerializedName("report_id")
    public String report_id;

    @SerializedName("input")
    public String input;

    @SerializedName("test_keys")
    public TestKeys test_keys;

    public class TestKeys {

        @SerializedName("blocking")
        public String blocking;

        @SerializedName("accessible")
        public String accessible;

        @SerializedName("tampering")
        public String tampering;

        @SerializedName("sent")
        public String sent;

        @SerializedName("received")
        public String received;

        @SerializedName("failure")
        public String failure;

        @SerializedName("header_field_name")
        public String header_field_name;

        @SerializedName("header_field_number")
        public String header_field_number;

        @SerializedName("header_field_value")
        public String header_field_value;

        @SerializedName("header_name_capitalization")
        public String header_name_capitalization;

        @SerializedName("request_line_capitalization")
        public String request_line_capitalization;

        @SerializedName("total")
        public String total;

        @SerializedName("server_address")
        public String server_address;

        @SerializedName("median_bitrate")
        public String median_bitrate;

        @SerializedName("min_playout_delay")
        public String min_playout_delay;

        @SerializedName("whatsapp_endpoints_status")
        public String whatsapp_endpoints_status;

        @SerializedName("whatsapp_web_status")
        public String whatsapp_web_status;

        @SerializedName("registration_server_status")
        public String registration_server_status;

        @SerializedName("facebook_tcp_blocking")
        public String facebook_tcp_blocking;

        @SerializedName("facebook_dns_blocking")
        public String facebook_dns_blocking;

        @SerializedName("telegram_http_blocking")
        public String telegram_http_blocking;

        @SerializedName("telegram_tcp_blocking")
        public String telegram_tcp_blocking;

        @SerializedName("telegram_web_status")
        public String telegram_web_status;
    }
}

