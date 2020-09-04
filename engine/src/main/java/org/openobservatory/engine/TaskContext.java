package org.openobservatory.engine;

/**
 * TaskContext allows to interrupt a long running task programmatically
 * or by setting a specific timeout in advance.
 */
public class TaskContext {
    protected oonimkall.Context ctx;

    /**
     * TaskContext(long) allows to construct a Context that expires
     * after the given amount of seconds is elapsed. A zero or negative
     * timeout value implies there is no timeout.
     */
    public TaskContext(long timeout) {
        ctx = oonimkall.Oonimkall.newContextWithTimeout(timeout);
    }

    /** TaskContext() constructs a context without timeout. */
    public TaskContext() {
        this(-1);
    }

    /**
     * cancel() is an idempotent method that cancels any operation
     * that is currently using this task context.
     */
    public void cancel() {
        ctx.cancel();
    }
}
