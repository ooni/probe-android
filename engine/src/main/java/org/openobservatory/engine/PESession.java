package org.openobservatory.engine;

import android.util.Log;

import com.google.gson.Gson;

import oonimkall.HTTPRequest;
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

    public OONIContext newContextWithTimeout(long timeout) {
        return new OONIContext(session.newContextWithTimeout(timeout));
    }

    public OONISubmitResults submit(OONIContext ctx, String measurement) throws Exception {
        return new OONISubmitResults(session.submit(ctx.ctx, measurement));
    }

    public OONICheckInResults checkIn(OONIContext ctx, OONICheckInConfig config) throws Exception {
        return new OONICheckInResults(session.checkIn(ctx.ctx, config.toOonimkallCheckInConfig()));
    }

    @Override
    public OONIRunDescriptor ooniRunFetch(OONIContext ctx, String probeServicesURL, long id) throws Exception {
        HTTPRequest request = new HTTPRequest();
        request.setMethod("GET");
        request.setURL(probeServicesURL + "/api/v2/oonirun/links/" + id);
        String response = session.httpDo(ctx.ctx, request).getBody();
        Log.d(PESession.class.getName(), response);
        return new Gson().fromJson(response, OONIRunDescriptor.class);
    }
}
