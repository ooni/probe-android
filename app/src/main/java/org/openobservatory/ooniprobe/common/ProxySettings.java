package org.openobservatory.ooniprobe.common;

import java.net.URI;
import java.net.URISyntaxException;

// ProxySettings contains the settings configured inside the proxy.
public class ProxySettings {
    public enum Protocol {
        NONE("none"),
        PSIPHON("psiphon"),
        SOCKS5("socks5");

        private String protocol;

        Protocol(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocol() {
            return protocol;
        }
    }

    // scheme is the proxy scheme (e.g., "psiphon", "socks5").
    public Protocol protocol = Protocol.NONE;

    // hostname is the hostname for custom proxies.
    public String hostname = "";

    // port is the port for custom proxies.
    public String port = "";

    // newProxySettings creates a new instance of the proxy settings from the current Preferences.
    public static ProxySettings newProxySettings(PreferenceManager pm) throws InvalidProxyURL {
        ProxySettings settings = new ProxySettings();
        // Make sure the protocol is one of the schemes we recognize.
        String protocol = pm.getProxyProtocol();
        if (protocol.equals(Protocol.NONE.getProtocol())) {
            settings.protocol = Protocol.NONE;
        } else if (protocol.equals(Protocol.PSIPHON.getProtocol())) {
            settings.protocol = Protocol.PSIPHON;
        } else if (protocol.equals(Protocol.SOCKS5.getProtocol())) {
            settings.protocol = Protocol.SOCKS5;
        } else {
            // This is where we will extend the code to add support for
            // more proxies, e.g., HTTP proxies.
            throw new InvalidProxyURL("unhandled URL scheme");
        }
        settings.hostname = pm.getProxyHostname();
        settings.port = pm.getProxyPort();

        // We are good, return to the caller.
        return settings;
    }

    public void saveProxy(PreferenceManager pm){
        pm.setProxyProtocol(protocol);
        pm.setProxyHostname(hostname);
        pm.setProxyPort(port);
    }

    public String getProxyString() throws URISyntaxException {
        if (protocol == Protocol.NONE)
            return "";
        else if (protocol == Protocol.PSIPHON)
            return "psiphon://";
        else if (protocol == Protocol.SOCKS5){
            // Alright, we now need to construct a new SOCKS5 URL. We are going to defer
            // doing that to the Java standard library (er, the Android stdlib).
            String urlStr = "socks5://" + hostname + ":" + port + "/";
            URI url = new URI(urlStr);
            return url.toASCIIString();
        }
        return "";
    }

    // InvalidProxyURL indicates that the proxy URL is not valid.
    public static class InvalidProxyURL extends Exception {
        InvalidProxyURL(String message, Throwable cause) {
            super(message, cause);
        }

        InvalidProxyURL(String message) {
            super(message);
        }
    }
}
