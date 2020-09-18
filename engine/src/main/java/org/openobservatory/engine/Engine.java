package org.openobservatory.engine;

import android.content.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Engine is a factory class for creating several kinds of tasks. We will use different
 * engines depending on the task that you wish to create.
 */
public final class Engine {
    private static Set<String> probeEngineTasks = new HashSet<>(Arrays.asList(
            "Telegram",
            "Dash",
            "Ndt",
            "Psiphon",
            "Tor",
            "Whatsapp",
            "FacebookMessenger",
            "HttpHeaderFieldManipulation",
            "HttpInvalidRequestLine",
            "WebConnectivity"
    ));

    /** getVersionMK returns the version of Measurement Kit we're using */
    @Deprecated
    public static String getVersionMK() {
        return io.ooni.mk.MKVersion.getVersionMK();
    }

    /** newUUID4 returns the a new UUID4 for this client  */
    public static String newUUID4() {
        return oonimkall.Oonimkall.newUUID4();
    }

    /** startExperimentTask starts the experiment described by the provided settings. */
    public static OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws OONIException {
        if (probeEngineTasks.contains(settings.taskName())) {
            try {
                return new PEMKTask(
                        oonimkall.Oonimkall.startTask(settings.serialization())
                );
            } catch (Exception exc) {
                throw new OONIException("cannot start OONI Probe Engine task", exc);
            }
        }
        return new MKMKTask(io.ooni.mk.MKAsyncTask.start(settings.serialization()));
    }

    /** maybeUpdateResources updates the bundled resources if needed */
    @Deprecated
    public static boolean maybeUpdateResources(Context context) {
        return io.ooni.mk.MKResourcesManager.maybeUpdateResources(context);
    }

    /** getCABundlePath returns the CA bundle path. */
    @Deprecated
    public static String getCABundlePath(Context context) {
        return io.ooni.mk.MKResourcesManager.getCABundlePath(context);
    }

    /** getCountryDBPath returns the GeoIP country DB path. */
    @Deprecated
    public static String getCountryDBPath(Context context) {
        return io.ooni.mk.MKResourcesManager.getCountryDBPath(context);
    }

    /** getASNDBPath returns the GeoIP ASN DB path. */
    @Deprecated
    public static String getASNDBPath(Context context) {
        return io.ooni.mk.MKResourcesManager.getASNDBPath(context);
    }

    /** newGeoIPLookupTask creates a new GeoIP lookup task. This version of
     * this factory is still using Measurement Kit. */
    @Deprecated
    public static OONIGeoIPLookupTask newGeoIPLookupTask() {
        return new MKGeoIPLookupTask();
    }

    /** newGeoIPLookupTask creates a new GeoIP lookup task. This version of
     * this factory is already using OONI Probe Engine. */
    public static OONIGeoIPLookupTask newGeoIPLookupTask(Context ctx) {
        return new PEGeoIPLookupTask();
    }

    /** newCollectorTask creates a new collector task. This version of
     * this factory is still using Measurement Kit. */
    @Deprecated
    public static OONICollectorTask newCollectorTask(String softwareName, String softwareVersion,
                                                     String caBundlePath) {
        return new MKCollectorTask(softwareName, softwareVersion, caBundlePath);
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
