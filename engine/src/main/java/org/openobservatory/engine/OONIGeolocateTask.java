package org.openobservatory.engine;

/**
 * OONIGeolocateTask allows you to perform geolocations. After the first
 * lookup is done, the result will be memoized by the code.
 */
public interface OONIGeolocateTask extends AutoCloseable, OONICancellable {
    OONIGeolocateResults run() throws OONIException;
}
