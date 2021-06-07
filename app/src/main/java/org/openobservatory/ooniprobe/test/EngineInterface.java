package org.openobservatory.ooniprobe.test;

import android.content.Context;

import org.openobservatory.engine.Engine;
import org.openobservatory.engine.OONILogger;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.engine.OONIMKTaskConfig;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONISessionConfig;

import java.io.IOException;

public class EngineInterface {

    public String newUUID4() {
        return Engine.newUUID4();
    }

    public OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws Exception {
        return Engine.startExperimentTask(settings);
    }

    public OONISession newSession(OONISessionConfig config) throws Exception {
        return Engine.newSession(config);
    }

    public OONISessionConfig getDefaultSessionConfig(Context ctx,
                                                     String softwareName,
                                                     String softwareVersion,
                                                     OONILogger logger,
                                                     String proxy) throws Exception {
        return Engine.getDefaultSessionConfig(ctx, softwareName, softwareVersion, logger, proxy);
    }

    public String getAssetsDir(Context ctx) throws IOException {
        return Engine.getAssetsDir(ctx);
    }

    public String getStateDir(Context ctx) throws IOException {
        return Engine.getStateDir(ctx);
    }

    public String getTempDir(Context ctx) throws IOException {
        return Engine.getTempDir(ctx);
    }

    public String getTunnelDir(Context ctx) throws IOException {
        return Engine.getTunnelDir(ctx);
    }
}
