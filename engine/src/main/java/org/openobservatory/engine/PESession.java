package org.openobservatory.engine;

import oonimkall.Oonimkall;
import oonimkall.Session;

public final class PESession implements OONISession {
    private Session session;

    public PESession(OONISessionConfig config) throws Exception {
        session = Oonimkall.newSession(config.toOonimkallSessionConfig());
    }

    public OONIContext newContext() {
        return newContextWithTimeout(-1);
    }

	public OONIRunFetchResponse ooniRunFetch(OONIContext ctx, long id) throws Exception {
		return new OONIRunFetchResponse(session.ooniRunFetch(ctx.ctx,id));
	}

	public OONIContext newContextWithTimeout(long timeout) {
        return new OONIContext(session.newContextWithTimeout(timeout));
    }

    public OONISubmitResults submit(OONIContext ctx, String measurement) throws Exception {
        return new OONISubmitResults(session.submit(ctx.ctx, measurement));
    }

    public OONICheckInResults checkIn(OONIContext ctx, OONICheckInConfig config) throws Exception {
        return new OONICheckInResults(session.checkIn(ctx.ctx, config.toOonimkallCheckInConfig()));
    }
}
