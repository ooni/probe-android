package org.openobservatory.engine;

/**
 * TaskContext allows to interrupt a long running task programmatically
 * or by setting a specific timeout in advance.
 */
public class TaskContext implements AutoCloseable {
    protected oonimkall.TaskContext ctx;

    /**
     * TaskContext(long) allows to construct a Context that expires
     * after the given amount of seconds is elapsed. A zero or negative
     * timeout value implies there is no timeout.
     */
    public TaskContext(long timeout) {
        ctx = oonimkall.Oonimkall.newTaskContextWithTimeout(timeout);
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

    /** close is like cancel() and it's here to implement AutoCloseable. */
    public void close() throws Exception { this.cancel(); }
}
