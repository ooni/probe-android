package org.openobservatory.engine;

/** GeoIPLookupResults contains the results of a GeoIP lookup */
public interface GeoIPLookupResults {
    /** isGood returns whether we succeeded. */
    public boolean isGood();

    /** getProbeIP returns the probe IP. */
    public String getProbeIP();

    /** getProbeASN returns the probe ASN. */
    public String getProbeASN();

    /** getProbeCC returns the probe CC. */
    public String getProbeCC();

    /** getProbeOrg returns the probe ASN organization. */
    public String getProbeOrg();

    /** getLogs returns the logs as one or more newline separated
     * lines containing only UTF-8 characters. */
    public String getLogs();
}
