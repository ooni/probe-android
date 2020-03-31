package org.openobservatory.engine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    /** startExperiment starts the experiment described by the provided settings. */
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
}
