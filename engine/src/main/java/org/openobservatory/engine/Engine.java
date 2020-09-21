package org.openobservatory.engine;

import android.content.Context;

import java.io.IOException;

import oonimkall.Oonimkall;
import oonimkall.Session;

/**
 * Engine is a factory class for creating several kinds of tasks. We will use different
 * engines depending on the task that you wish to create.
 */
public final class Engine {
    /** newUUID4 returns the a new UUID4 for this client  */
    public static String newUUID4() {
        return oonimkall.Oonimkall.newUUID4();
    }

    /** startExperimentTask starts the experiment described by the provided settings. */
    public static OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws OONIException {
        try {
            return new PEMKTask(
                    oonimkall.Oonimkall.startTask(settings.serialization())
            );
        } catch (Exception exc) {
            throw new OONIException("cannot start OONI Probe Engine task", exc);
        }
    }

    /** newGeoIPLookupTask creates a new GeoIP lookup task. This version of
     * this factory is already using OONI Probe Engine. */
    public static OONIGeoIPLookupTask newGeoIPLookupTask(Context ctx,
                                                         String softwareName,
                                                         String softwareVersion,
                                                         long timeout) {
        try {
            //TODO could DefaultSessionConfig be useful?
            Session session = Oonimkall.newSession(DefaultSessionConfig.getDefaultSessionConfig(ctx, softwareName, softwareVersion));
            return new PEGeoIPLookupTask(session.newGeolocateTask(timeout));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** newCollectorTask creates a new collector task. This version of
     * this factory is already using OONI Probe Engine. */
    public static OONICollectorTask newCollectorTask(Context ctx, String softwareName,
                                                     String softwareVersion) throws IOException {
        return new PECollectorTask(getAssetsDir(ctx), softwareName, softwareVersion, getStateDir(ctx), getTempDir(ctx));
    }

    /** newSession returns a new OONISession instance. */
    public static OONISession newSession(OONISessionConfig config) throws OONIException {
        return new PESession(config);
    }

    /** getAssetsDir returns the assets directory for the current context. The
     * assets directory is the directory where the OONI Probe Engine should store
     * the assets it requires, e.g., the MaxMind DB files. */
    public static String getAssetsDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getFilesDir(), "assets").getCanonicalPath();
    }

    /** getStateDir returns the state directory for the current context. The
     * state directory is the directory where the OONI Probe Engine should store
     * internal state variables (e.g. the orchestra credentials). */
    public static String getStateDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getFilesDir(), "state").getCanonicalPath();
    }

    /** getTempDir returns the temporary directory for the current context. The
     * temporary directory is the directory where the OONI Probe Engine should store
     * the temporary files that are managed by a Session. */
    public static String getTempDir(Context ctx) throws IOException {
        return new java.io.File(ctx.getCacheDir(), "").getCanonicalPath();
    }
}
