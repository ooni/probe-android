package org.openobservatory.engine;

import oonimkall.GeolocateTask;

final class PEGeolocateTask implements OONIGeolocateTask {
    private GeolocateTask task;

    public PEGeolocateTask(GeolocateTask task) {
        this.task = task;
    }

    @Override
    public OONIGeolocateResults run() throws OONIException {
        try {
            return new OONIGeolocateResults(task.run());
        } catch (Exception exc) {
            throw new OONIException("PEGeolocateTask.run failed", exc);
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
