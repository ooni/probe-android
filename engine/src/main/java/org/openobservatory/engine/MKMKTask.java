package org.openobservatory.engine;

final class MKMKTask implements OONIMKTask {
    private io.ooni.mk.MKAsyncTask task;

    public MKMKTask(io.ooni.mk.MKAsyncTask task) {
        this.task = task;
    }

    @Override
    public boolean canInterrupt() {
        return false;
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
