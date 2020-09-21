package org.openobservatory.engine;

import oonimkall.GeolocateTask;

final class PEGeoIPLookupTask implements OONIGeoIPLookupTask {
    private GeolocateTask task;

    public PEGeoIPLookupTask(GeolocateTask task) {
        this.task = task;
    }

    @Override
    public PEGeoIPLookupResults run() throws OONIException {
        try {
            return new PEGeoIPLookupResults(new OONIGeolocateResults(task.run()));
        } catch (Exception exc) {
            throw new OONIException("PEGeoIPLookupTask.run failed", exc);
        }
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public void close() throws Exception {
        task.close();
    }
}