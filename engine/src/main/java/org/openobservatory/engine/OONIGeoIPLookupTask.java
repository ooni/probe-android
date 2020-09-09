package org.openobservatory.engine;

/** OONIGeoIPLookupTask performs a GeoIP lookup */
public interface OONIGeoIPLookupTask {
    /** setTimeout sets the number of seconds after which pending
     * requests are aborted by the underlying engine. */
    void setTimeout(long timeout);

    /** setCABundlePath sets the path of the CA bundle to use. */
    void setCABundlePath(String path);

    /** setCountryDBPath sets the path of the MaxMind country
     * database to use. */
    void setCountryDBPath(String path);

    /** setASNDBPath sets the path of the MaxMind ASN
     * database to use. */
    void setASNDBPath(String path);

    /** perform performs a GeoIP lookup with current settings. */
    OONIGeoIPLookupResults perform();
}
