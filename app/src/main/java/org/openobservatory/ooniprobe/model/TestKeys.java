package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.Locale;

public class TestKeys {
	@SerializedName("blocking")
	public String blocking;
	@SerializedName("accessible")
	public String accessible;
	@SerializedName("sent")
	public ArrayList<String> sent;
	@SerializedName("received")
	public ArrayList<String> received;
	@SerializedName("failure")
	public String failure;
	@SerializedName("server_address")
	public String server_address;
	public String server_name;
	public String server_country;
	@SerializedName("whatsapp_endpoints_status")
	public String whatsapp_endpoints_status;
	@SerializedName("whatsapp_web_status")
	public String whatsapp_web_status;
	@SerializedName("registration_server_status")
	public String registration_server_status;
	@SerializedName("facebook_tcp_blocking")
	public Boolean facebook_tcp_blocking;
	@SerializedName("facebook_dns_blocking")
	public Boolean facebook_dns_blocking;
	@SerializedName("telegram_http_blocking")
	public Boolean telegram_http_blocking;
	@SerializedName("telegram_tcp_blocking")
	public Boolean telegram_tcp_blocking;
	@SerializedName("telegram_web_status")
	public String telegram_web_status;
	@SerializedName("simple")
	public Simple simple;
	@SerializedName("advanced")
	public Advanced advanced;
	@SerializedName("tampering")
	public Tampering tampering;

	public String getWebsiteBlocking(Context ctx) {
		if (this.blocking != null) {
			if (this.blocking.equals("dns"))
				return ctx.getString(R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_DNS);
			else if (this.blocking.equals("tcp_ip"))
				return ctx.getString(R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_TCPIP);
			else if (this.blocking.equals("http-diff"))
				return ctx.getString(R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_HTTPDiff);
			else if (this.blocking.equals("http-failure"))
				return ctx.getString(R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_HTTPFailure);
			else
				return ctx.getString(R.string.TestResults_NotAvailable);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getWhatsappEndpointStatus(Context ctx) {
		if (this.whatsapp_endpoints_status != null) {
			if (this.whatsapp_endpoints_status.equals("blocked"))
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Failed);
			return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getWhatsappWebStatus(Context ctx) {
		if (this.whatsapp_web_status != null) {
			if (this.whatsapp_web_status.equals("blocked"))
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Failed);
			return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getWhatsappRegistrationStatus(Context ctx) {
		if (this.registration_server_status != null) {
			if (this.registration_server_status.equals("blocked"))
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Failed);
			return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getTelegramEndpointStatus(Context ctx) {
		if (this.telegram_http_blocking != null && this.telegram_tcp_blocking != null) {
			if (this.telegram_http_blocking || this.telegram_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
			else
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getTelegramWebStatus(Context ctx) {
		if (this.telegram_web_status != null) {
			if (this.telegram_web_status.equals("blocked"))
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
			return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getTelegramBlocking(Context ctx) {
		if (this.telegram_http_blocking != null && this.telegram_tcp_blocking != null) {
			if (this.telegram_http_blocking && this.telegram_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Content_Paragraph_HTTPandTCPIP);
			else if (this.telegram_http_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Content_Paragraph_HTTPOnly);
			else if (this.telegram_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Content_Paragraph_TCPIPOnly);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getFacebookMessengerDns(Context ctx) {
		if (this.facebook_dns_blocking != null) {
			if (this.facebook_dns_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Failed);
			else
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getFacebookMessengerTcp(Context ctx) {
		if (this.facebook_tcp_blocking != null) {
			if (this.facebook_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Failed);
			else
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Okay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getFacebookMessengerBlocking(Context ctx) {
		if (this.facebook_dns_blocking != null && this.facebook_tcp_blocking != null) {
			if (this.facebook_dns_blocking && this.facebook_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_BlockingReason_DNSandTCPIP);
			else if (this.facebook_dns_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_BlockingReason_DNSOnly);
			else if (this.facebook_tcp_blocking)
				return ctx.getString(R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_BlockingReason_TCPIPOnly);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getUpload(Context ctx) {
		//TODO check this.simple.upload not null?
		if (this.simple != null) {
			return setFractionalDigits(getScaledValue(this.simple.upload));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getUploadUnit(Context ctx) {
		if (this.simple != null) {
			return getUnit(this.simple.upload, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getDownload(Context ctx) {
		if (this.simple != null) {
			return setFractionalDigits(getScaledValue(this.simple.download));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getDownloadUnit(Context ctx) {
		if (this.simple != null) {
			return getUnit(this.simple.download, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private double getScaledValue(double value) {
		if (value < 100)
			return value;
		else if (value < 100000)
			return value / 1000;
		else
			return value / 1000000;
	}

	private String setFractionalDigits(double value) {
		if (value < 10)
			return String.format(Locale.getDefault(), "%.2f", value);
		else
			return String.format(Locale.getDefault(), "%.1f", value);
	}

	private String getUnit(double value, Context ctx) {
		//We assume there is no Tbit/s (for now!)
		if (value < 100)
			return ctx.getString(R.string.TestResults_Kbps);
		else if (value < 100000)
			return ctx.getString(R.string.TestResults_Mbps);
		else
			return ctx.getString(R.string.TestResults_Gbps);
	}

	public String getMedianBitrate(Context ctx) {
		//TODO here in iOS I check for a null value
		if (simple.median_bitrate != null) {
			return setFractionalDigits(getScaledValue(simple.median_bitrate));
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getMedianBitrateUnit(Context ctx) {
		if (simple.median_bitrate != null) {
			return getUnit(simple.median_bitrate, ctx);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public String getVideoQuality(Context ctx, Boolean extended) {
		if (simple.median_bitrate != null) {
			return minimumBitrateForVideo(simple.median_bitrate, extended);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	private String minimumBitrateForVideo(double videoQuality, Boolean extended) {
		if (videoQuality < 600)
			return "240p";
		else if (videoQuality < 1000)
			return "360p";
		else if (videoQuality < 2500)
			return "480p";
		else if (videoQuality < 5000)
			return extended ? "720p (HD)" : "720p";
		else if (videoQuality < 8000)
			return extended ? "1080p (full HD)" : "1080p";
		else if (videoQuality < 16000)
			return extended ? "1440p (2k)" : "1440p";
		else
			return extended ? "2160p (4k)" : "2160p";
	}

	public String getPlayoutDelay(Context ctx) {
		if (simple.min_playout_delay != null) {
			return String.format(Locale.getDefault(), "%.2f", simple.min_playout_delay);
		}
		return ctx.getString(R.string.TestResults_NotAvailable);
	}

	public static class Simple {
		@SerializedName("upload")
		public double upload;
		@SerializedName("download")
		public double download;
		@SerializedName("ping")
		public String ping;
		@SerializedName("median_bitrate")
		public Double median_bitrate;
		@SerializedName("min_playout_delay")
		public Double min_playout_delay;
	}

	public static class Advanced {
		@SerializedName("packet_loss")
		public String packet_loss;
		@SerializedName("out_of_order")
		public String out_of_order;
		@SerializedName("avg_rtt")
		public String avg_rtt;
		@SerializedName("max_rtt")
		public String max_rtt;
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

		public class TamperingObj {
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
