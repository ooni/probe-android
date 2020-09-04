package org.openobservatory.engine;

/** SubmitTask is a task for submitting a measurement. This class is an
 * AutoCloseable and should be used within a try-with-resources statement. */
public class SubmitTask implements AutoCloseable {
    private static final long defaultCloseTimeout = 1; // seconds

    protected oonimkall.SubmitTask task;
    private long closeTimeout;

    /**
     * SubmitTask(ctx, sess) is like SubmitTask(ctx, sess, timeout) except that
     * the defaultCloseTimeout value will be used for the timeout argument.
     */
    public SubmitTask(TaskContext ctx, Session sess) throws EngineException {
        this(ctx, sess, defaultCloseTimeout);
    }

    /**
     * SubmitTask(ctx, sess, timeout) creates a new SubmitTask. The operation of
     * creating a new task entails some network operations, which are bounded using
     * the context argument. The session us used for creating the SubmitTask and
     * henceforth by the SubmitTask. The timeout is the maximum number of seconds
     * you're willing to wait in close() for the SubmitTask to cleanup.
     */
    public SubmitTask(TaskContext ctx, Session sess, long timeout) throws EngineException {
        try {
            task = oonimkall.Oonimkall.newSubmitTask(ctx.ctx, sess.sess);
        } catch (Exception exc) {
            throw new EngineException("newSubmitTask failed", exc);
        }
        this.closeTimeout = timeout;
    }

    /**
     * submit submits the given measurement bounded in time by the
     * specified context and returns the results.
     */
    public SubmitResults submit(TaskContext ctx, String measurement) throws EngineException {
        try {
            return new SubmitResults(task.submit(ctx.ctx, measurement));
        } catch (Exception exc) {
            throw new EngineException("submit failed", exc);
        }
    }

    /** close releases the resources used by SubmitTask. This method will try
     * to close any lingering report for up to the timeout configured in the
     * constructor, of for up to the default close timeout otherwise. */
    public void close() throws EngineException {
        TaskContext ctx = new TaskContext(closeTimeout);
        try {
            task.close(ctx.ctx);
        } catch (Exception exc) {
            throw new EngineException("close failed", exc);
        }
    }
}
