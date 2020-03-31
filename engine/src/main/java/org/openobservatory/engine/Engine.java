package org.openobservatory.engine;

import android.content.Context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Engine is a factory class for creating several kinds of tasks. We will use different
 * engines depending on the task that you wish to create.
 */
public class Engine {
    private static Set<String> probeEngineTasks = new HashSet<>(Arrays.asList(
            "Telegram"
    ));

    /** getVersionMK returns the version of Measurement Kit we're using */
    public static String getVersionMK() {
        return io.ooni.mk.MKVersion.getVersionMK();
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

    /** newGeoIPLookupTask creates a new GeoIP lookup task. */
    public static GeoIPLookupTask newGeoIPLookupTask() {
        return new MKGeoIPLookupTaskAdapter();
    }

    /** newCollectorTask creates a new collector task. */
    public static CollectorTask newCollectorTask(String softwareName, String softwareVersion,
                                                 String caBundlePath) {
        return new MKReporterTaskAdapter(softwareName, softwareVersion, caBundlePath);
    }

    /** newOrchestraTask creates a new orchestra task. */
    public static OrchestraTask newOrchestraTask(String softwareName, String softwareVersion,
                                                 Vector<String> supportedTests,
                                                 String deviceToken, String secretsFile) {
        return new MKOrchestraTaskAdapter(softwareName, softwareVersion, supportedTests,
                deviceToken, secretsFile);
    }
}
