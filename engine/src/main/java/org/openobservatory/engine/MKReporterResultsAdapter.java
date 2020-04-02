package org.openobservatory.engine;

class MKReporterResultsAdapter implements CollectorResults {
    private io.ooni.mk.MKReporterResults results;

    public MKReporterResultsAdapter(io.ooni.mk.MKReporterResults results) {
        this.results = results;
    }

    public boolean isGood() {
        return this.results.isGood();
    }

    public String getReason() {
        return this.results.getReason();
    }

    public String getLogs() {
        return this.results.getLogs();
    }

    public String getUpdatedSerializedMeasurement() {
        return this.results.getUpdatedSerializedMeasurement();
    }

    public String getUpdatedReportID() {
        return this.results.getUpdatedReportID();
    }
}
