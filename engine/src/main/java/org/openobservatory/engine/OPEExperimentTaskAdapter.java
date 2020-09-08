package org.openobservatory.engine;

class OPEExperimentTaskAdapter implements ExperimentTask {
    private oonimkall.Task task;

    public OPEExperimentTaskAdapter(oonimkall.Task task) {
        this.task = task;
    }

    public boolean canInterrupt() {
        return true;
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
