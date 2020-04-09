package org.openobservatory.engine;

class MKExperimentTaskAdapter implements ExperimentTask {
    private io.ooni.mk.MKAsyncTask task;

    public MKExperimentTaskAdapter(io.ooni.mk.MKAsyncTask task) {
        this.task = task;
    }

    public boolean canInterrupt() {
        return false;
    }

    public void interrupt() {
        this.task.interrupt();
    }

    public boolean isDone() {
        return this.task.isDone();
    }

    public String waitForNextEvent() {
        return this.task.waitForNextEvent();
    }
}
