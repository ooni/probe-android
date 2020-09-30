package org.openobservatory.engine;

import oonimkall.Oonimkall;
import oonimkall.Session;

final class PESession implements OONISession {
    private Session session;

    public PESession(OONISessionConfig config) throws OONIException {
        try {
            session = Oonimkall.newSession(config.toOonimkallSessionConfig());
        } catch (Exception exc) {
            throw new OONIException("Oonimkall.newSession failed", exc);
        }
    }

    /** geolocate returns the probe geolocation. */
    public OONIGeolocateResults geolocate(OONIContext ctx) throws OONIException {
        try {
            return new OONIGeolocateResults(session.geolocate(ctx.ctx));
        } catch (Exception exc) {
            throw new OONIException("session.geolocate failed", exc);
        }
    }

    /** newContext creates a new OONIContext instance. */
    public OONIContext newContext() {
        return newContextWithTimeout(-1);
    }

    /**
     * newContextWithTimeout creates a new OONIContext instance that times
     * out after the specified number of seconds. A zero or negative timeout
     * is equivalent to create a OONIContext without a timeout.
     */
    public OONIContext newContextWithTimeout(long timeout) {
        return new OONIContext(session.newContextWithTimeout(timeout));
    }

    /** submit submits a measurement and returns the submission results. */
    public OONISubmitResults submit(OONIContext ctx, String measurement) throws OONIException {
        try {
            return new OONISubmitResults(session.submit(ctx.ctx, measurement));
        } catch (Exception exc) {
            throw new OONIException("", exc);
        }
    }
}
