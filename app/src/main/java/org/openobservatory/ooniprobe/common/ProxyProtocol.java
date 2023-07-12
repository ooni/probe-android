package org.openobservatory.ooniprobe.common;

/** ProxyProtocol enumerates all the kind of proxy we support. */
public enum ProxyProtocol {
    NONE("none"),
    PSIPHON("psiphon"),
    TOR("tor"),
    TORSF("torsf"),
    SOCKS5("socks5");

    private String protocol;

    ProxyProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}
