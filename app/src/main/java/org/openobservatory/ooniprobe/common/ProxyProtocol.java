package org.openobservatory.ooniprobe.common;

/** ProxyProtocol enumerates all the kind of proxy we support. */
public enum ProxyProtocol {
    NONE("none"),
    PSIPHON("psiphon"),
    SOCKS5("socks5"),
    HTTP("http"),
    HTTPS("https");

    private String protocol;

    ProxyProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}
