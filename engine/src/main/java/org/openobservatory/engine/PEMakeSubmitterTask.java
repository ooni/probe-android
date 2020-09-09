package org.openobservatory.engine;

import oonimkall.MakeSubmitterTask;

final class PEMakeSubmitterTask implements OONIMakeSubmitterTask {
    private MakeSubmitterTask task;

    public PEMakeSubmitterTask(MakeSubmitterTask task) {
        this.task = task;
    }

    @Override
    public OONISubmitter run() throws OONIException {
        try {
            return new PESubmitter(task.run());
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
