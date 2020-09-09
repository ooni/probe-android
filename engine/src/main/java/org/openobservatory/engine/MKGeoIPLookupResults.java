package org.openobservatory.engine;

final class MKGeoIPLookupResults implements OONIGeoIPLookupResults {
    private io.ooni.mk.MKGeoIPLookupResults results;

    public MKGeoIPLookupResults(io.ooni.mk.MKGeoIPLookupResults results) {
        this.results = results;
    }

    @Override
    public boolean isGood() {
        return this.results.isGood();
    }

    @Override
    public String getProbeIP() {
        return this.results.getProbeIP();
    }

    @Override
    public String getProbeASN() {
        return this.results.getProbeASN();
    }

    @Override
    public String getProbeCC() {
        return this.results.getProbeCC();
    }

    @Override
    public String getProbeOrg() {
        return this.results.getProbeOrg();
    }

    @Override
    public String getLogs() {
        return this.results.getLogs();
    }
}
