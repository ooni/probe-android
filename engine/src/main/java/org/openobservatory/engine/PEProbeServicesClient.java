package org.openobservatory.engine;

import oonimkall.MakeSubmitterTask;

final class PEProbeServicesClient implements OONIProbeServicesClient {
    private MakeSubmitterTask task;

    public PEProbeServicesClient(MakeSubmitterTask task) {
        this.task = task;
    }

    @Override
    public OONIReport openReport() throws OONIException {
        try {
            return new PEReport(task.run());
        } catch (Exception exc) {
            throw new OONIException("PEMakeSubmitterTask.run failed", exc);
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
