package org.openobservatory.engine;

final class MKCollectorTask implements OONICollectorTask {
    private io.ooni.mk.MKReporterTask task;

    public MKCollectorTask(String softwareName, String softwareVersion, String caBundlePath) {
        task = new io.ooni.mk.MKReporterTask(softwareName, softwareVersion, caBundlePath);
    }

    public OONICollectorResults maybeDiscoverAndSubmit(String measurement, long timeout) {
        return new MKCollectorResults(task.maybeDiscoverAndSubmit(measurement, timeout));
    }
}
