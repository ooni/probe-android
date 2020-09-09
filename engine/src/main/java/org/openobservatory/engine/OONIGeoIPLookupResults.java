package org.openobservatory.engine;

/** OONIGeoIPLookupResults contains the results of a GeoIP lookup */
public interface OONIGeoIPLookupResults {
    /** isGood returns whether we succeeded. */
    boolean isGood();

    /** getProbeIP returns the probe IP. */
    String getProbeIP();

    /** getProbeASN returns the probe ASN. */
    String getProbeASN();

    /** getProbeCC returns the probe CC. */
    String getProbeCC();

    /** getProbeOrg returns the probe ASN organization. */
    String getProbeOrg();

    /** getLogs returns the logs as one or more newline separated
     * lines containing only UTF-8 characters. */
    String getLogs();
}
