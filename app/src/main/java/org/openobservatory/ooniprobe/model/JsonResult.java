package org.openobservatory.ooniprobe.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class JsonResult {
	@SerializedName("probe_asn")
	public String probeAsn;
	@SerializedName("probe_cc")
	public String probeCc;
	@SerializedName("test_start_time")
	public Date testStartTime;
	@SerializedName("measurement_start_time")
	public Date measurementStartTime;
	@SerializedName("test_runtime")
	public Double test_runtime;
	@SerializedName("probe_ip")
	public String probeIp;
	@SerializedName("report_id")
	public String reportId;
	@SerializedName("input")
	public String input;
	@SerializedName("test_keys")
	public TestKeys testKeys;

	public static class TestKeys {
		@SerializedName("blocking")
		public Boolean blocking;
		@SerializedName("accessible")
		public String accessible;
		@SerializedName("sent")
		public ArrayList sent;
		@SerializedName("received")
		public ArrayList received;
		@SerializedName("failure")
		public String failure;
		@SerializedName("header_field_name")
		public String headerFieldName;
		@SerializedName("header_field_number")
		public String headerFieldNumber;
		@SerializedName("header_field_value")
		public String headerFieldValue;
		@SerializedName("header_name_capitalization")
		public String headerNameCapitalization;
		@SerializedName("request_line_capitalization")
		public String requestLineCapitalization;
		@SerializedName("total")
		public String total;
		@SerializedName("server_address")
		public String serverAddress;
		public String serverName;
		public String serverCountry;
		@SerializedName("median_bitrate")
		public String medianBitrate;
		@SerializedName("min_playout_delay")
		public String minPlayoutDelay;
		@SerializedName("whatsapp_endpoints_status")
		public String whatsappEndpointsStatus;
		@SerializedName("whatsapp_web_status")
		public String whatsappWebStatus;
		@SerializedName("registration_server_status")
		public String registrationServerStatus;
		@SerializedName("facebook_tcp_blocking")
		public Boolean facebookTcpBlocking;
		@SerializedName("facebook_dns_blocking")
		public Boolean facebookDnsBlocking;
		@SerializedName("telegram_http_blocking")
		public Boolean telegramHttpBlocking;
		@SerializedName("telegram_tcp_blocking")
		public Boolean telegramTcpBlocking;
		@SerializedName("telegram_web_status")
		public String telegramWebStatus;
		@SerializedName("simple")
		public Simple simple;
		@SerializedName("advanced")
		public Advanced advanced;
		@SerializedName("tampering")
		public Tampering tampering;

		public static class Simple {
			@SerializedName("upload")
			public String upload;
			@SerializedName("download")
			public String download;
			@SerializedName("ping")
			public String ping;
		}

		public static class Advanced {
			@SerializedName("packet_loss")
			public String packetLoss;
			@SerializedName("out_of_order")
			public String outOfOrder;
			@SerializedName("avg_rtt")
			public String avgRtt;
			@SerializedName("max_rtt")
			public String maxRtt;
			@SerializedName("mss")
			public String mss;
			@SerializedName("timeouts")
			public String timeouts;
		}

		public static class Tampering {
			public boolean value;

			public Tampering(boolean value) {
				this.value = value;
			}

			public class TamperingObj { // TODO LORENZO check header_name_diff
				@SerializedName("header_field_name") boolean header_field_name;
				@SerializedName("header_field_number") boolean header_field_number;
				@SerializedName("header_field_value") boolean header_field_value;
				@SerializedName("header_name_capitalization") boolean header_name_capitalization;
				@SerializedName("request_line_capitalization") boolean request_line_capitalization;
				@SerializedName("total") boolean total;

				public boolean isAnomaly() {
					return header_field_name || header_field_number || header_field_value || header_name_capitalization || request_line_capitalization || total;
				}
			}
		}
	}
}