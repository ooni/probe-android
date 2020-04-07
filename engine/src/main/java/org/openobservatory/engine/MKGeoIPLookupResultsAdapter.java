package org.openobservatory.engine;

class MKGeoIPLookupResultsAdapter implements GeoIPLookupResults {
    private io.ooni.mk.MKGeoIPLookupResults results;

    public MKGeoIPLookupResultsAdapter(io.ooni.mk.MKGeoIPLookupResults results) {
        this.results = results;
    }

    public boolean isGood() {
        return this.results.isGood();
    }

    public String getProbeIP() {
        return this.results.getProbeIP();
    }

    public String getProbeASN() {
        return this.results.getProbeASN();
    }

    public String getProbeCC() {
        return this.results.getProbeCC();
    }

    public String getProbeOrg() {
        return this.results.getProbeOrg();
    }

    public String getLogs() {
        return this.results.getLogs();
    }
}
