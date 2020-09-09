package org.openobservatory.engine;

final class PEGeoIPLookupResults implements OONIGeoIPLookupResults {
    private OONIGeolocateResults results;
    private Exception error;

    public PEGeoIPLookupResults(OONIGeolocateResults results) {
        this.results = results;
    }

    public PEGeoIPLookupResults(Exception error) {
        this.error = error;
    }

    @Override
    public boolean isGood() {
        return error == null;
    }

    @Override
    public String getLogs() {
        if (error != null) {
            return error.toString();
        }
        return "";
    }

    @Override
    public String getProbeASN() {
        if (error != null) {
            return "";
        }
        return results.ASN;
    }

    @Override
    public String getProbeCC() {
        if (error != null) {
            return "";
        }
        return results.country;
    }

    @Override
    public String getProbeIP() {
        if (error != null) {
            return "";
        }
        return results.IP;
    }

    @Override
    public String getProbeOrg() {
        if (error != null) {
            return "";
        }
        return results.org;
    }
}
