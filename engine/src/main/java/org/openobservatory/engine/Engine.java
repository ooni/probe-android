package org.openobservatory.engine;

import android.content.Context;

import java.io.IOException;

/**
 * Engine is a factory class for creating several kinds of tasks. We will use different
 * engines depending on the task that you wish to create.
 */
public final class Engine {
    /** newUUID4 returns the a new UUID4 for this client */
    public static String newUUID4() {
        return oonimkall.Oonimkall.newUUID4();
    }

    /** startExperimentTask starts the experiment described by the provided settings. */
    public static OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws Exception {
        return new PEMKTask(oonimkall.Oonimkall.startTask(settings.serialization()));
    }

    /** newSession returns a new OONISession instance. */
    public static OONISession newSession(OONISessionConfig config) throws Exception {
        return new PESession(config);
    }

    /** getDefaultSessionConfig returns a new SessionConfig with default parameters. */
    public static OONISessionConfig getDefaultSessionConfig(Context ctx,
                                                            String softwareName,
                                                            String softwareVersion,
                                                            OONILogger logger,
                                                            String optionalProxy) throws Exception {
        OONISessionConfig config = new OONISessionConfig();
        config.logger = new LoggerComposed(logger, new LoggerAndroid());
        config.proxy = optionalProxy;
        config.softwareName = softwareName;
        config.softwareVersion = softwareVersion;
        config.verbose = false;
        config.assetsDir = Engine.getAssetsDir(ctx);
        config.stateDir = Engine.getStateDir(ctx);
        config.tempDir = Engine.getTempDir(ctx);
        config.tunnelDir = Engine.getTunnelDir(ctx);
        return config;
    }

    /**
     * getAssetsDir returns the assets directory for the current context. The
     * assets directory is the directory where the OONI Probe Engine should store
     * the assets it requires, e.g., the MaxMind DB files.
     */
    public static String getAssetsDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getFilesDir(), "assets").getCanonicalPath();
    }

    /**
     * getStateDir returns the state directory for the current context. The
     * state directory is the directory where the OONI Probe Engine should store
     * internal state variables (e.g. the orchestra credentials).
     */
    public static String getStateDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getFilesDir(), "state").getCanonicalPath();
    }

    /**
     * getTempDir returns the temporary directory for the current context. The
     * temporary directory is the directory where the OONI Probe Engine should store
     * the temporary files that are managed by a Session.
     */
    public static String getTempDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getCacheDir(), "").getCanonicalPath();
    }

    /**
     * getTunnelDir returns the directory where to store persistent tunnel state.
     */
    public static String getTunnelDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getFilesDir(), "tunnel").getCanonicalPath();
    }
}
