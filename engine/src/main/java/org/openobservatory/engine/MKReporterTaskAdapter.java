package org.openobservatory.engine;

class MKReporterTaskAdapter implements CollectorTask {
    private io.ooni.mk.MKReporterTask task;

    public MKReporterTaskAdapter(String softwareName, String softwareVersion, String caBundlePath) {
        task = new io.ooni.mk.MKReporterTask(softwareName, softwareVersion, caBundlePath);
    }

    public CollectorResults maybeDiscoverAndSubmit(String measurement, long timeout) {
        return new MKReporterResultsAdapter(task.maybeDiscoverAndSubmit(measurement, timeout));
    }
}
