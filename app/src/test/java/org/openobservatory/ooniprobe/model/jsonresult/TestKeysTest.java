package org.openobservatory.ooniprobe.model.jsonresult;

import org.junit.Test;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TestKeysTest extends RobolectricAbstractTest {
    private static final String BLANK = "";
    private static final String ZERO_ZERO = String.format(Locale.getDefault(), "%.0f", 0f);
    private static final String ZERO_ONE = String.format(Locale.getDefault(), "%.1f", 0f);
    private static final String ZERO_TWO = String.format(Locale.getDefault(), "%.2f", 0f);
    private static final String ZERO_THREE = String.format(Locale.getDefault(), "%.3f", 0f);
    private static final String TEN_ONE = String.format(Locale.getDefault(), "%.1f", 10f);
    private static final String HUNDRED_THREE = String.format(Locale.getDefault(), "%.3f", 100f);

    @Test
    public void getWebsitesBlocking() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_NotAvailable);
        testKeys.blocking = "invalid";
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_NotAvailable);
        testKeys.blocking = "dns";
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_DNS);
        testKeys.blocking = "tcp_ip";
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_TCPIP);
        testKeys.blocking = "http-diff";
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_HTTPDiff);
        testKeys.blocking = "http-failure";
        assertEquals(testKeys.getWebsiteBlocking(), R.string.TestResults_Details_Websites_LikelyBlocked_BlockingReason_HTTPFailure);
    }

    @Test
    public void whatsappEndpointStatus() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_NotAvailable);
        testKeys.whatsapp_endpoints_status = TestKeys.BLOCKED;
        assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Failed);
        testKeys.whatsapp_endpoints_status = BLANK;
        assertEquals(testKeys.getWhatsappEndpointStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Application_Label_Okay);
    }

    @Test
    public void whatsappWebStatus() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_NotAvailable);
        testKeys.whatsapp_web_status = TestKeys.BLOCKED;
        assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_WebApp_Label_Failed);
        testKeys.whatsapp_web_status = BLANK;
        assertEquals(testKeys.getWhatsappWebStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_WebApp_Label_Okay);
    }

    @Test
    public void whatsappRegistrationStatus() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_NotAvailable);
        testKeys.registration_server_status = TestKeys.BLOCKED;
        assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Failed);
        testKeys.registration_server_status = BLANK;
        assertEquals(testKeys.getWhatsappRegistrationStatus(), R.string.TestResults_Details_InstantMessaging_WhatsApp_Registrations_Label_Okay);
    }

    @Test
    public void telegramEndpointStatus() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_NotAvailable);
        testKeys.telegram_http_blocking = true;
        testKeys.telegram_tcp_blocking = true;
        assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
        testKeys.telegram_http_blocking = true;
        testKeys.telegram_tcp_blocking = false;
        assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
        testKeys.telegram_http_blocking = false;
        testKeys.telegram_tcp_blocking = true;
        assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Failed);
        testKeys.telegram_http_blocking = false;
        testKeys.telegram_tcp_blocking = false;
        assertEquals(testKeys.getTelegramEndpointStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_Application_Label_Okay);
    }

    @Test
    public void telegramWebStatus() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_NotAvailable);
        testKeys.telegram_web_status = TestKeys.BLOCKED;
        assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_WebApp_Label_Failed);
        testKeys.telegram_web_status = BLANK;
        assertEquals(testKeys.getTelegramWebStatus(), R.string.TestResults_Details_InstantMessaging_Telegram_WebApp_Label_Okay);
    }

    @Test
    public void facebookMessengerDns() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_NotAvailable);
        testKeys.facebook_dns_blocking = true;
        assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_DNS_Label_Failed);
        testKeys.facebook_dns_blocking = false;
        assertEquals(testKeys.getFacebookMessengerDns(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_DNS_Label_Okay);
    }

    @Test
    public void facebookMessengerTcp() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_NotAvailable);
        testKeys.facebook_tcp_blocking = true;
        assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_TCP_Label_Failed);
        testKeys.facebook_tcp_blocking = false;
        assertEquals(testKeys.getFacebookMessengerTcp(), R.string.TestResults_Details_InstantMessaging_FacebookMessenger_TCP_Label_Okay);
    }

    @Test
    public void getRiseupVPNApiStatus() {
        TestKeys testKeys = new TestKeys();
        testKeys.ca_cert_status = true;
        assertEquals(testKeys.getRiseupVPNApiStatus(), R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Okay);
        testKeys.api_failure = "fail";
        testKeys.ca_cert_status = true;
        assertEquals(testKeys.getRiseupVPNApiStatus(), R.string.TestResults_Overview_Circumvention_RiseupVPN_Api_Blocked);
        testKeys.api_failure = null;
        testKeys.ca_cert_status = false;
        assertEquals(testKeys.getRiseupVPNApiStatus(), R.string.TestResults_Overview_Circumvention_RiseupVPN_Api_Blocked);
    }

    @Test
    public void getRiseupVPNOpenvpnGatewayStatus() {
        TestKeys testKeys = new TestKeys();
        TestKeys.GatewayConnection diffConnection = new TestKeys.GatewayConnection();
        diffConnection.transport_type = "different";
        TestKeys.GatewayConnection openVpnConnection = new TestKeys.GatewayConnection();
        openVpnConnection.transport_type = "openvpn";

        assertEquals(
                c.getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Okay),
                testKeys.getRiseupVPNOpenvpnGatewayStatus(c)
        );

        testKeys.failing_gateways = new TestKeys.GatewayConnection[]{diffConnection};
        assertEquals(
                c.getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Okay),
                testKeys.getRiseupVPNOpenvpnGatewayStatus(c)
        );

        testKeys.failing_gateways = new TestKeys.GatewayConnection[]{openVpnConnection};
        assertEquals(
                "1 blocked",
                testKeys.getRiseupVPNOpenvpnGatewayStatus(c)
        );
    }

    @Test
    public void getRiseupVPNBridgedGatewayStatus() {
        TestKeys testKeys = new TestKeys();
        TestKeys.GatewayConnection diffConnection = new TestKeys.GatewayConnection();
        diffConnection.transport_type = "different";
        TestKeys.GatewayConnection bridgedConnection = new TestKeys.GatewayConnection();
        bridgedConnection.transport_type = "obfs4";

        assertEquals(
                c.getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Okay),
                testKeys.getRiseupVPNBridgedGatewayStatus(c)
        );

        testKeys.failing_gateways = new TestKeys.GatewayConnection[]{diffConnection};
        assertEquals(
                c.getString(R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Okay),
                testKeys.getRiseupVPNBridgedGatewayStatus(c)
        );

        testKeys.failing_gateways = new TestKeys.GatewayConnection[]{bridgedConnection};
        assertEquals(
                "1 blocked",
                testKeys.getRiseupVPNBridgedGatewayStatus(c)
        );
    }

    @Test
    public void upload() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getUpload(c), c.getString(R.string.TestResults_NotAvailable));

        testKeys.protocol = 7;
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getUpload(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.upload = 0d;
        assertEquals(testKeys.getUpload(c), ZERO_TWO);
        testKeys.summary.upload = 10d;
        assertEquals(testKeys.getUpload(c), TEN_ONE);

        testKeys.protocol = null;
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getUpload(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple.upload = 0d;
        assertEquals(testKeys.getUpload(c), ZERO_TWO);
        testKeys.simple.upload = 10d;
        assertEquals(testKeys.getUpload(c), TEN_ONE);
    }

    @Test
    public void uploadUnit() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_NotAvailable);

        testKeys.protocol = 7;
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_NotAvailable);
        testKeys.summary.upload = 0d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Kbps);
        testKeys.summary.upload = 1000d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Mbps);
        testKeys.summary.upload = 1000d * 1000d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Gbps);

        testKeys.protocol = null;
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_NotAvailable);
        testKeys.simple.upload = 0d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Kbps);
        testKeys.simple.upload = 1000d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Mbps);
        testKeys.simple.upload = 1000d * 1000d;
        assertEquals(testKeys.getUploadUnit(), R.string.TestResults_Gbps);
    }

    @Test
    public void download() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getDownload(c), c.getString(R.string.TestResults_NotAvailable));

        testKeys.protocol = 7;
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getDownload(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.download = 0d;
        assertEquals(testKeys.getDownload(c), ZERO_TWO);
        testKeys.summary.download = 10d;
        assertEquals(testKeys.getDownload(c), TEN_ONE);

        testKeys.protocol = null;
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getDownload(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple.download = 0d;
        assertEquals(testKeys.getDownload(c), ZERO_TWO);
        testKeys.simple.download = 10d;
        assertEquals(testKeys.getDownload(c), TEN_ONE);
    }

    @Test
    public void downloadUnit() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_NotAvailable);

        testKeys.protocol = 7;
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_NotAvailable);
        testKeys.summary.download = 0d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Kbps);
        testKeys.summary.download = 1000d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Mbps);
        testKeys.summary.download = 1000d * 1000d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Gbps);

        testKeys.protocol = null;
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_NotAvailable);
        testKeys.simple.download = 0d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Kbps);
        testKeys.simple.download = 1000d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Mbps);
        testKeys.simple.download = 1000d * 1000d;
        assertEquals(testKeys.getDownloadUnit(), R.string.TestResults_Gbps);
    }

    @Test
    public void ping() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getPing(c), c.getString(R.string.TestResults_NotAvailable));

        testKeys.protocol = 7;
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.ping = 0d;
        assertEquals(testKeys.getPing(c), ZERO_ONE);

        testKeys.protocol = null;
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple.ping = 0d;
        assertEquals(testKeys.getPing(c), ZERO_ONE);
    }

    @Test
    public void serverDetails() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getServerDetails(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.server_name = BLANK;
        testKeys.server_country = null;
        assertEquals(testKeys.getServerDetails(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.server_name = null;
        testKeys.server_country = BLANK;
        assertEquals(testKeys.getServerDetails(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.server_name = BLANK;
        testKeys.server_country = BLANK;
        assertEquals(testKeys.getServerDetails(c), BLANK + " - " + BLANK);
    }

    @Test
    public void packetLoss() {
        TestKeys testKeys = new TestKeys();

        testKeys.protocol = 7;
        assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.retransmit_rate = 0d;
        assertEquals(testKeys.getPacketLoss(c), ZERO_THREE);
        testKeys.summary.retransmit_rate = 1d;
        assertEquals(testKeys.getPacketLoss(c), HUNDRED_THREE);

        testKeys.protocol = null;
        assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced = new TestKeys.Advanced();
        assertEquals(testKeys.getPacketLoss(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced.packet_loss = 0d;
        assertEquals(testKeys.getPacketLoss(c), ZERO_THREE);
        testKeys.advanced.packet_loss = 1d;
        assertEquals(testKeys.getPacketLoss(c), HUNDRED_THREE);
    }

    @Test
    public void averagePing() {
        TestKeys testKeys = new TestKeys();

        testKeys.protocol = 7;
        assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.avg_rtt = 0d;
        assertEquals(testKeys.getAveragePing(c), ZERO_ONE);

        testKeys.protocol = null;
        assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced = new TestKeys.Advanced();
        assertEquals(testKeys.getAveragePing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced.avg_rtt = 0d;
        assertEquals(testKeys.getAveragePing(c), ZERO_ONE);
    }

    @Test
    public void maxPing() {
        TestKeys testKeys = new TestKeys();

        testKeys.protocol = 7;
        assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.max_rtt = 0d;
        assertEquals(testKeys.getMaxPing(c), ZERO_ONE);

        testKeys.protocol = null;
        assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced = new TestKeys.Advanced();
        assertEquals(testKeys.getMaxPing(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced.max_rtt = 0d;
        assertEquals(testKeys.getMaxPing(c), ZERO_ONE);
    }

    @Test
    public void mss() {
        TestKeys testKeys = new TestKeys();

        testKeys.protocol = 7;
        assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary = new TestKeys.Summary();
        assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.summary.mss = 0d;
        assertEquals(testKeys.getMSS(c), ZERO_ZERO);

        testKeys.protocol = null;
        assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced = new TestKeys.Advanced();
        assertEquals(testKeys.getMSS(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.advanced.mss = 0d;
        assertEquals(testKeys.getMSS(c), ZERO_ZERO);
    }

    @Test
    public void medianBitrate() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getMedianBitrate(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getMedianBitrate(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple.median_bitrate = 0d;
        assertEquals(testKeys.getMedianBitrate(c), ZERO_TWO);
        testKeys.simple.median_bitrate = 10d;
        assertEquals(testKeys.getMedianBitrate(c), TEN_ONE);
    }

    @Test
    public void medianBitrateUnit() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_NotAvailable);
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_NotAvailable);
        testKeys.simple.median_bitrate = 0d;
        assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Kbps);
        testKeys.simple.median_bitrate = 1000d;
        assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Mbps);
        testKeys.simple.median_bitrate = 1000d * 1000d;
        assertEquals(testKeys.getMedianBitrateUnit(), R.string.TestResults_Gbps);
    }

    @Test
    public void videoQuality() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getVideoQuality(false), R.string.TestResults_NotAvailable);
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getVideoQuality(false), R.string.TestResults_NotAvailable);
        testKeys.simple.median_bitrate = 0d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r240p);
        testKeys.simple.median_bitrate = 600d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r360p);
        testKeys.simple.median_bitrate = 1000d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r480p);
        testKeys.simple.median_bitrate = 2500d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r720p);
        assertEquals(testKeys.getVideoQuality(true), R.string.r720p_ext);
        testKeys.simple.median_bitrate = 5000d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r1080p);
        assertEquals(testKeys.getVideoQuality(true), R.string.r1080p_ext);
        testKeys.simple.median_bitrate = 8000d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r1440p);
        assertEquals(testKeys.getVideoQuality(true), R.string.r1440p_ext);
        testKeys.simple.median_bitrate = 16000d;
        assertEquals(testKeys.getVideoQuality(false), R.string.r2160p);
        assertEquals(testKeys.getVideoQuality(true), R.string.r2160p_ext);
    }

    @Test
    public void playoutDelay() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getPlayoutDelay(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple = new TestKeys.Simple();
        assertEquals(testKeys.getPlayoutDelay(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.simple.min_playout_delay = 0d;
        assertEquals(testKeys.getPlayoutDelay(c), ZERO_TWO);
    }

    @Test
    public void getBootstrapTime() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getBootstrapTime(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.bootstrap_time = 12.991;
        assertEquals(testKeys.getBootstrapTime(c), "12.99 s");
    }

    @Test
    public void getBridges() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getBridges(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.obfs4_accessible = 1L;
        testKeys.obfs4_total = 2L;
        assertEquals(testKeys.getBridges(c), "1/2 OK");
    }


    @Test
    public void getAuthorities() {
        TestKeys testKeys = new TestKeys();
        assertEquals(testKeys.getAuthorities(c), c.getString(R.string.TestResults_NotAvailable));
        testKeys.or_port_dirauth_accessible = 1L;
        testKeys.or_port_dirauth_total = 2L;
        assertEquals(testKeys.getAuthorities(c), "1/2 OK");
    }
}
