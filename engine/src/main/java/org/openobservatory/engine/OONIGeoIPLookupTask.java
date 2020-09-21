package org.openobservatory.engine;

/** OONIGeoIPLookupTask performs a GeoIP lookup */
public interface OONIGeoIPLookupTask extends AutoCloseable, OONICancellable {
    /** perform performs a GeoIP lookup with current settings. */
    OONIGeoIPLookupResults run() throws OONIException;
}
