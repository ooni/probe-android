package org.openobservatory.engine;

/**
 * Session is a container for running OONI experiments as well
 * as other ancillary tasks (e.g resubmission, geolocation).
 *
 * The Session is an AutoCloseable and shall be used within a
 * try-with-resources construct to ensure it's closed.
 *
 * The plan is that eventually all operations will use a Session
 * however for now only some operations use it.
 *
 * Design: https://github.com/ooni/probe-engine/issues/893#issuecomment-686613123
 */
public class Session implements AutoCloseable {
    protected oonimkall.Session sess;

    /** Session creates a new Session instance using the given config. */
    public Session(SessionConfig config) throws EngineException {
        try {
            sess = oonimkall.Oonimkall.newSession(config.config);
        } catch (Exception exc) {
            throw new EngineException("newSession failed", exc);
        }
    }

    /** geolocate returns the location of a probe. */
    public GeolocateResults geolocate(TaskContext ctx) throws EngineException {
        try {
            return new GeolocateResults(sess.geolocate(ctx.ctx));
        } catch (Exception exc) {
            throw new EngineException("geolocate failed", exc);
        }
    }

    /** close releases the resources used by this Session. */
    public void close() throws EngineException {
        try {
            sess.close();
        } catch (Exception exc) {
            throw new EngineException("close failed", exc);
        }
    }
}
