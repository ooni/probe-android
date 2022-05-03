package org.openobservatory.engine;

import oonimkall.GeolocateResults;

/** OONIGeolocateResults contains the results of OONIGeolocateTask. */
public final class OONIGeolocateResults {
    /** ASN is the probe ASN. */
    private final String ASN;

    /** country is the probe country. */
    private final String country;

    /** IP is the probe IP. */
    private final String IP;

    /** org is the probe ASN organization. */
    private final String org;

    protected OONIGeolocateResults(GeolocateResults r) {
        ASN = r.getASN();
        country = r.getCountry();
        IP = r.getIP();
        org = r.getOrg();
    }

    public String getASN() {
        return ASN;
    }

    public String getCountry() {
        return country;
    }

    public String getIP() {
        return IP;
    }

    public String getOrg() {
        return org;
    }
}
