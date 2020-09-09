package org.openobservatory.engine;

final class PEMKTask implements OONIMKTask {
    private oonimkall.Task task;

    public PEMKTask(oonimkall.Task task) {
        this.task = task;
    }

    @Override
    public boolean canInterrupt() {
        return true;
    }

    @Override
    public void interrupt() {
        this.task.interrupt();
    }

    @Override
    public boolean isDone() {
        return this.task.isDone();
    }

    @Override
    public String waitForNextEvent() {
        return this.task.waitForNextEvent();
    }
}
