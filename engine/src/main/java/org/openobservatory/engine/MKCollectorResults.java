package org.openobservatory.engine;

final class MKCollectorResults implements OONICollectorResults {
    private io.ooni.mk.MKReporterResults results;

    public MKCollectorResults(io.ooni.mk.MKReporterResults results) {
        this.results = results;
    }

    @Override
    public boolean isGood() {
        return this.results.isGood();
    }

    @Override
    public String getReason() {
        return this.results.getReason();
    }

    @Override
    public String getLogs() {
        return this.results.getLogs();
    }

    @Override
    public String getUpdatedSerializedMeasurement() {
        return this.results.getUpdatedSerializedMeasurement();
    }

    @Override
    public String getUpdatedReportID() {
        return this.results.getUpdatedReportID();
    }
}
