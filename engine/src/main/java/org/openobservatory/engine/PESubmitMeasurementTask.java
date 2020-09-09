package org.openobservatory.engine;

import oonimkall.SubmitMeasurementTask;

final class PESubmitMeasurementTask implements OONISubmitMeasurementTask {
    private SubmitMeasurementTask task;

    public PESubmitMeasurementTask(SubmitMeasurementTask task) {
        this.task = task;
    }

    @Override
    public OONISubmitResults run(String measurement) throws OONIException {
        try {
            return new OONISubmitResults(task.run(measurement));
        } catch (Exception exc) {
            throw new OONIException("PESubmitMeasurementTask.run failed", exc);
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
