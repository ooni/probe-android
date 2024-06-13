package org.openobservatory.engine;

import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    public OONIRunDescriptor getLatestOONIRunLink(OONIContext ctx, String probeServicesURL, long id) throws Exception {
        HTTPRequest request = new HTTPRequest();
        request.setMethod("GET");
        request.setUrl(probeServicesURL + "/api/v2/oonirun/links/" + id);
        String response = session.httpDo(ctx.ctx, request).getBody();
        return new Gson().fromJson(response, OONIRunDescriptor.class);
    }

    @Override
    public OONIRunRevisions getOONIRunLinkRevisions(OONIContext ooniContext, @NotNull String probeServicesURL, long runId) throws Exception {
        HTTPRequest request = new HTTPRequest();
        request.setMethod("GET");
        request.setUrl(probeServicesURL + "/api/v2/oonirun/links/" + runId + "/revisions");
        String response = session.httpDo(ooniContext.ctx, request).getBody();
        OONIRunRevisions revisions = new Gson().fromJson(response, OONIRunRevisions.class);

        //remove the first element of the list, which is the latest revision
        revisions.getRevisions().remove(0);

        return revisions;
    }
}
