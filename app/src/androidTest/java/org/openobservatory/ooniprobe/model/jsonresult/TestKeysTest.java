package org.openobservatory.ooniprobe.model.jsonresult;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;

import java.util.Locale;

import androidx.test.filters.SmallTest;

@SmallTest public class TestKeysTest extends AbstractTest {
	private static final String BLANK = "";
	private static final String ZERO_ZERO = String.format(Locale.getDefault(), "%.0f", 0f);
	private static final String ZERO_ONE = String.format(Locale.getDefault(), "%.1f", 0f);
	private static final String ZERO_TWO = String.format(Locale.getDefault(), "%.2f", 0f);
	private static final String ZERO_THREE = String.format(Locale.getDefault(), "%.3f", 0f);
	private static final String TEN_ONE = String.format(Locale.getDefault(), "%.1f", 10f);
	private static final String HUNDRED_ONE = String.format(Locale.getDefault(), "%.1f", 100f);
	private static final String HUNDRED_THREE = String.format(Locale.getDefault(), "%.3f", 100f);

	@Test public void whatsappEndpointStatus() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_NotAvailable);
		testKeys.whatsapp_endpoints_status = TestKeys.BLOCKED;
		Assert.assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Failed);
		testKeys.whatsapp_endpoints_status = BLANK;
		Assert.assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Okay);
	}

	@Test public void whatsappWebStatus() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_NotAvailable);
		testKeys.whatsapp_web_status = TestKeys.BLOCKED;
		Assert.assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_WebApp_Label_Failed);
		testKeys.whatsapp_web_status = BLANK;
		Assert.assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_WebApp_Label_Okay);
	}

	@Test public void whatsappRegistrationStatus() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_NotAvailable);
		testKeys.registration_server_status = TestKeys.BLOCKED;
		Assert.assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Failed);
		testKeys.registration_server_status = BLANK;
		Assert.assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Okay);
	}

	@Test public void telegramEndpointStatus() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_NotAvailable);
		testKeys.telegram_http_blocking = true;
		testKeys.telegram_tcp_blocking = true;
		Assert.assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
		testKeys.telegram_http_blocking = true;
		testKeys.telegram_tcp_blocking = false;
		Assert.assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
		testKeys.telegram_http_blocking = false;
		testKeys.telegram_tcp_blocking = true;
		Assert.assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
		testKeys.telegram_http_blocking = false;
		testKeys.telegram_tcp_blocking = false;
		Assert.assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Okay);
	}

	@Test public void telegramWebStatus() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_NotAvailable);
		testKeys.telegram_web_status = TestKeys.BLOCKED;
		Assert.assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_WebApp_Label_Failed);
		testKeys.telegram_web_status = BLANK;
		Assert.assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_WebApp_Label_Okay);
	}

	@Test public void facebookMessengerDns() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_NotAvailable);
		testKeys.facebook_dns_blocking = true;
		Assert.assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_DNS_Label_Failed);
		testKeys.facebook_dns_blocking = false;
		Assert.assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_DNS_Label_Okay);
	}

	@Test public void facebookMessengerTcp() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_NotAvailable);
		testKeys.facebook_tcp_blocking = true;
		Assert.assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_TCP_Label_Failed);
		testKeys.facebook_tcp_blocking = false;
		Assert.assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_TCP_Label_Okay);
	}

	@Test public void upload() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getUpload(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getUpload(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple.upload = 0d;
		Assert.assertEquals(testKeys.getUpload(c), ZERO_TWO);
		testKeys.simple.upload = 10d;
		Assert.assertEquals(testKeys.getUpload(c), TEN_ONE);
	}

	@Test public void uploadUnit() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getUploadUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getUploadUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple.upload = 0d;
		Assert.assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Kbps);
		testKeys.simple.upload = 1000d;
		Assert.assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Mbps);
		testKeys.simple.upload = 1000d * 1000d;
		Assert.assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Gbps);
	}

	@Test public void download() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getDownload(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getDownload(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple.download = 0d;
		Assert.assertEquals(testKeys.getDownload(c), ZERO_TWO);
		testKeys.simple.download = 10d;
		Assert.assertEquals(testKeys.getDownload(c), TEN_ONE);
	}

	@Test public void downloadUnit() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple.download = 0d;
		Assert.assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Kbps);
		testKeys.simple.download = 1000d;
		Assert.assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Mbps);
		testKeys.simple.download = 1000d * 1000d;
		Assert.assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Gbps);
	}

	@Test public void ping() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getPing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getPing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple.ping = 0d;
		Assert.assertEquals(testKeys.getPing(c), ZERO_ONE);
	}

	@Test public void server() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getServer(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.server_name = BLANK;
		testKeys.server_country = null;
		Assert.assertEquals(testKeys.getServer(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.server_name = null;
		testKeys.server_country = BLANK;
		Assert.assertEquals(testKeys.getServer(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.server_name = BLANK;
		testKeys.server_country = BLANK;
		Assert.assertEquals(testKeys.getServer(c), BLANK + " - " + BLANK);
	}

	@Test public void packetLoss() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.packet_loss = 0d;
		Assert.assertEquals(testKeys.getPacketLoss(c), ZERO_THREE);
		testKeys.advanced.packet_loss = 1d;
		Assert.assertEquals(testKeys.getPacketLoss(c), HUNDRED_THREE);
	}

	@Test public void outOfOrder() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getOutOfOrder(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getOutOfOrder(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.out_of_order = 0d;
		Assert.assertEquals(testKeys.getOutOfOrder(c), ZERO_ONE);
		testKeys.advanced.out_of_order = 1d;
		Assert.assertEquals(testKeys.getOutOfOrder(c), HUNDRED_ONE);
	}

	@Test public void averagePing() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.avg_rtt = 0d;
		Assert.assertEquals(testKeys.getAveragePing(c), ZERO_ONE);
	}

	@Test public void maxPing() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.max_rtt = 0d;
		Assert.assertEquals(testKeys.getMaxPing(c), ZERO_ONE);
	}

	@Test public void mss() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.mss = 0d;
		Assert.assertEquals(testKeys.getMSS(c), ZERO_ZERO);
	}

	@Test public void timeouts() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getTimeouts(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced = new TestKeys.Advanced();
		Assert.assertEquals(testKeys.getTimeouts(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.advanced.timeouts = 0d;
		Assert.assertEquals(testKeys.getTimeouts(c), ZERO_ZERO);
	}

	@Test public void medianBitrate() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getMedianBitrate(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getMedianBitrate(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple.median_bitrate = 0d;
		Assert.assertEquals(testKeys.getMedianBitrate(c), ZERO_TWO);
		testKeys.simple.median_bitrate = 10d;
		Assert.assertEquals(testKeys.getMedianBitrate(c), TEN_ONE);
	}

	@Test public void medianBitrateUnit() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_NotAvailable);
		testKeys.simple.median_bitrate = 0d;
		Assert.assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Kbps);
		testKeys.simple.median_bitrate = 1000d;
		Assert.assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Mbps);
		testKeys.simple.median_bitrate = 1000d * 1000d;
		Assert.assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Gbps);
	}

	@Test public void videoQuality() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getVideoQuality(false), R.string.TestResults_NotAvailable);
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getVideoQuality(false), R.string.TestResults_NotAvailable);
		testKeys.simple.median_bitrate = 0d;
		Assert.assertEquals(testKeys.getVideoQuality(false), R.string.r240p);
	}

	@Test public void playoutDelay() {
		TestKeys testKeys = new TestKeys();
		Assert.assertEquals(testKeys.getPlayoutDelay(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple = new TestKeys.Simple();
		Assert.assertEquals(testKeys.getPlayoutDelay(c), c.getString(R.string.TestResults_NotAvailable));
		testKeys.simple.min_playout_delay = 0d;
		Assert.assertEquals(testKeys.getPlayoutDelay(c), ZERO_TWO);
	}
}
