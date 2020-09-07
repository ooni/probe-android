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
public class Engine {
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
    public static String getVersionMK() {
        return io.ooni.mk.MKVersion.getVersionMK();
    }

    /** newUUID4 returns the a new UUID4 for this client  */
    public static String newUUID4() {
        return oonimkall.Oonimkall.newUUID4();
    }

    /** startExperimentTask starts the experiment described by the provided settings. */
    public static ExperimentTask startExperimentTask(ExperimentSettings settings) throws EngineException {
        if (probeEngineTasks.contains(settings.taskName())) {
            try {
                return new OONIProbeEngineTaskAdapter(
                        oonimkall.Oonimkall.startTask(settings.serialization())
                );
            } catch (Exception exc) {
                throw new EngineException("cannot start OONI Probe Engine task", exc);
            }
        }
        return new MKExperimentTaskAdapter(io.ooni.mk.MKAsyncTask.start(settings.serialization()));
    }

    /** maybeUpdateResources updates the bundled resources if needed */
    public static boolean maybeUpdateResources(Context context) {
        return io.ooni.mk.MKResourcesManager.maybeUpdateResources(context);
    }

    /** getCABundlePath returns the CA bundle path. */
    public static String getCABundlePath(Context context) {
        return io.ooni.mk.MKResourcesManager.getCABundlePath(context);
    }

    /** getCountryDBPath returns the GeoIP country DB path. */
    public static String getCountryDBPath(Context context) {
        return io.ooni.mk.MKResourcesManager.getCountryDBPath(context);
    }

    /** getASNDBPath returns the GeoIP ASN DB path. */
    public static String getASNDBPath(Context context) {
        return io.ooni.mk.MKResourcesManager.getASNDBPath(context);
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

    /** newGeoIPLookupTask creates a new GeoIP lookup task. */
    public static GeoIPLookupTask newGeoIPLookupTask() {
        return new MKGeoIPLookupTaskAdapter();
    }

    /** newCollectorTask creates a new collector task. */
    public static CollectorTask newCollectorTask(String softwareName, String softwareVersion,
                                                 String caBundlePath) {
        return new MKReporterTaskAdapter(softwareName, softwareVersion, caBundlePath);
    }
}
