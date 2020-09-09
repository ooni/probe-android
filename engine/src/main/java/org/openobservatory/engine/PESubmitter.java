package org.openobservatory.engine;

import oonimkall.Submitter;

final class PESubmitter implements OONISubmitter {
    private Submitter submitter;

    public PESubmitter(Submitter submitter) {
        this.submitter = submitter;
    }

    @Override
    public OONISubmitMeasurementTask newSubmitMeasurementTask(long timeout) throws OONIException {
        try {
            return new PESubmitMeasurementTask(submitter.newSubmitMeasurementTask(timeout));
        } catch (Exception exc) {
            throw new OONIException("PESubmitter.newSubmitMeasurementTask failed", exc);
        }
    }

    @Override
    public void close() throws Exception {
        submitter.close();
    }
}
