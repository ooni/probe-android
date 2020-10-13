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

    public OONIGeolocateResults geolocate(OONIContext ctx) throws OONIException {
        try {
            return new OONIGeolocateResults(session.geolocate(ctx.ctx));
        } catch (Exception exc) {
            throw new OONIException("session.geolocate failed", exc);
        }
    }

    public void maybeUpdateResources(OONIContext ctx) throws OONIException {
        try {
            session.maybeUpdateResources(ctx.ctx);
        } catch (Exception exc) {
            throw new OONIException("session.maybeUpdateResources failed", exc);
        }
    }

    public OONIContext newContext() {
        return newContextWithTimeout(-1);
    }

    public OONIContext newContextWithTimeout(long timeout) {
        return new OONIContext(session.newContextWithTimeout(timeout));
    }

    public OONISubmitResults submit(OONIContext ctx, String measurement) throws OONIException {
        try {
            return new OONISubmitResults(session.submit(ctx.ctx, measurement));
        } catch (Exception exc) {
            throw new OONIException("", exc);
        }
    }
}
