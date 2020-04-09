package org.openobservatory.engine;

/** GeoIPLookupTask performs a GeoIP lookup */
public interface GeoIPLookupTask {
    /** setTimeout sets the number of seconds after which pending
     * requests are aborted by the underlying engine. */
    public void setTimeout(long timeout);

    /** setCABundlePath sets the path of the CA bundle to use. */
    public void setCABundlePath(String path);

    /** setCountryDBPath sets the path of the MaxMind country
     * database to use. */
    public void setCountryDBPath(String path);

    /** setASNDBPath sets the path of the MaxMind ASN
     * database to use. */
    public void setASNDBPath(String path);

    /** perform performs a GeoIP lookup with current settings. */
    public GeoIPLookupResults perform();
}
