package org.openobservatory.engine;

import oonimkall.GeolocateResults;

/** OONIGeolocateResults contains the results of OONIGeolocateTask. */
public final class OONIGeolocateResults {
    /** ASN is the probe ASN. */
    String ASN;

    /** country is the probe country. */
    String country;

    /** IP is the probe IP. */
    String IP;

    /** org is the probe ASN organization. */
    String org;

    protected OONIGeolocateResults(GeolocateResults r) {
        ASN = r.getASN();
        country = r.getCountry();
        IP = r.getIP();
        org = r.getOrg();
    }
}
