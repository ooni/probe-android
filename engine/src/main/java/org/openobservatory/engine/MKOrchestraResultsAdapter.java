package org.openobservatory.engine;

class MKOrchestraResultsAdapter implements OrchestraResults {
    private io.ooni.mk.MKOrchestraResults results;

    public MKOrchestraResultsAdapter(io.ooni.mk.MKOrchestraResults results) {
        this.results = results;
    }

    public boolean isGood() {
        return results.isGood();
    }

    public String getLogs() {
        return results.getLogs();
    }
}
