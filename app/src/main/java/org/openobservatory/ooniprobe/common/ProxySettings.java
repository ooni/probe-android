package org.openobservatory.ooniprobe.common;

import com.google.common.net.InetAddresses;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * ProxySettings contains the settings configured inside the proxy. Please, see the
 * documentation of proxy activity for the design rationale.
 */
public class ProxySettings {
    /** scheme is the proxy scheme (e.g., "psiphon", "tor", "torsf", "socks5"). */
    public ProxyProtocol protocol = ProxyProtocol.NONE;

    /** hostname is the hostname for custom proxies. */
    public String hostname = "";

    /** port is the port for custom proxies. */
    public String port = "";

    /** newProxySettings creates a new instance of the proxy settings from the current Preferences. */
    public static ProxySettings newProxySettings(PreferenceManager pm) throws InvalidProxyURL {
        ProxySettings settings = new ProxySettings();

        // Make sure the protocol is one of the schemes we recognize.
        String protocol = pm.getProxyProtocol();
        if (protocol.equals(ProxyProtocol.NONE.getProtocol())) {
            settings.protocol = ProxyProtocol.NONE;
        } else if (protocol.equals(ProxyProtocol.PSIPHON.getProtocol())) {
            settings.protocol = ProxyProtocol.PSIPHON;
        } else if (protocol.equals(ProxyProtocol.TOR.getProtocol())) {
            settings.protocol = ProxyProtocol.TOR;
        } else if (protocol.equals(ProxyProtocol.TORSF.getProtocol())) {
            settings.protocol = ProxyProtocol.TORSF;
        } else if (protocol.equals(ProxyProtocol.SOCKS5.getProtocol())) {
            settings.protocol = ProxyProtocol.SOCKS5;
        } else {
            // This exception indicates that we need to extend the code to support
            // more proxies, e.g., HTTP proxies.
            throw new InvalidProxyURL("unhandled URL scheme");
        }
        settings.hostname = pm.getProxyHostname();
        settings.port = pm.getProxyPort();

        // We are good, return to the caller.
        return settings;
    }

    /** saveProxy saves the current state into the preference manager. */
    public void saveProxy(PreferenceManager pm) {
        pm.setProxyProtocol(protocol);
        pm.setProxyHostname(hostname);
        pm.setProxyPort(port);
    }

    // isIPv6 tells us whether the input is an IPv6 address.
    @SuppressWarnings("UnstableApiUsage")
    private boolean isIPv6(String hostname) {
        try {
            // The UnknownHostException may suggest we're doing a domain
            // name resolution here. While this is possible in theory, we
            // check whether we're dealing with a literal IP address in
            // advance. In such a case getByName won't attempt to resolve
            // anything because it's already given an IP address.
            return InetAddresses.isInetAddress(hostname) &&
                    InetAddress.getByName(hostname) instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /** getProxyString returns to you the proxy string you should pass to oonimkall. */
    public String getProxyString() throws URISyntaxException {
        if (protocol == ProxyProtocol.NONE) {
            return "";
        }
        if (protocol == ProxyProtocol.PSIPHON) {
            return "psiphon:///";
        }
        if (protocol == ProxyProtocol.TOR) {
            return "tor:///";
        }
        if (protocol == ProxyProtocol.TORSF) {
            return "torsf:///";
        }
        if (protocol == ProxyProtocol.SOCKS5) {
            // Alright, we now need to construct a new SOCKS5 URL. We are going to defer
            // doing that to the Java standard library (er, the Android stdlib).
            String urlStr = "socks5://" + hostname + ":" + port + "/";
            if (isIPv6(hostname)) {
                urlStr = "socks5://[" + hostname + "]:" + port + "/"; // IPv6 must be quoted in URLs
            }
            URI url = new URI(urlStr);
            return url.toASCIIString();
        }
        return "";
    }

    /** InvalidProxyURL indicates that the proxy URL is not valid. */
    public static class InvalidProxyURL extends Exception {
        InvalidProxyURL(String message) {
            super(message);
        }
    }
}
