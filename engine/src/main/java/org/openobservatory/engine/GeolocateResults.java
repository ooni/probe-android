package org.openobservatory.engine;

/** GeolocateResults contains the results of geolocating this probe. */
public class GeolocateResults {
    protected oonimkall.GeolocateResults results;

    protected GeolocateResults(oonimkall.GeolocateResults results) {
        this.results = results;
    }

    /** getASN returns the probe ASN. */
    public String getASN() {
        return results.getASN();
    }

    /** getCountry returns the probe country. */
    public String getCountry() {
        return results.getCountry();
    }

    /** getIP returns the probe IP. */
    public String getIP() {
        return results.getIP();
    }

    /** getOrg returns the probe ASN organization. */
    public String getOrg() {
        return results.getOrg();
    }
}