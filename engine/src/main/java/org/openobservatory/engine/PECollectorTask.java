package org.openobservatory.engine;

import oonimkall.MakeSubmitterTask;
import oonimkall.Oonimkall;
import oonimkall.Session;
import oonimkall.SessionConfig;
import oonimkall.SubmitMeasurementTask;
import oonimkall.SubmitResults;
import oonimkall.Submitter;

final class PECollectorTask implements OONICollectorTask {

    private class State {
        private MakeSubmitterTask makeSubmitterTask;
        private Session session;
        private Submitter submitter;

        public State(SessionConfig config) throws Exception {
            session = Oonimkall.newSession(config);
            try {
                makeSubmitterTask = session.newMakeSubmitterTask(10 /* seconds */);
            } catch (Exception exc) {

            }
        }
    };

    private SessionConfig config;
    private MakeSubmitterTask makeSubmitterTask;
    private Session session;
    private Submitter submitter;

    public PECollectorTask(String assetsDir,
                           String softwareName,
                           String softwareVersion,
                           String stateDir,
                           String tempDir) {
        config = new SessionConfig();
        config.setAssetsDir(assetsDir);
        config.setLogger(null); // TODO(bassosimone): implement
        config.setSoftwareName(softwareName);
        config.setSoftwareVersion(softwareVersion);
        config.setStateDir(stateDir);
        config.setTempDir(tempDir);
        config.setVerbose(true);
    }

    private SubmitMeasurementTask getSubmitMeasurementTask(long timeout) throws Exception {
        if (session == null) {
            session = Oonimkall.newSession(config);
        }
        if (makeSubmitterTask == null) {
            makeSubmitterTask = session.newMakeSubmitterTask(10 /* seconds */);
        }
        if (submitter == null) {
            submitter = makeSubmitterTask.run();
        }
        return submitter.newSubmitMeasurementTask(timeout);
    }

    public OONICollectorResults maybeDiscoverAndSubmit(String measurement, long timeout) {
        SubmitMeasurementTask task;
        try {
            task = getSubmitMeasurementTask(timeout);
        } catch (Exception exc) {
            return new PECollectorResults(exc);
        }
        SubmitResults results;
        try {
            results = task.run(measurement);
        } catch (Exception exc) {
            return new PECollectorResults(exc);
        } finally {
            try {
                task.close();
            } catch (Exception exc) {
                return new PECollectorResults(exc);
            }
        }
        return new PECollectorResults(results);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void finalize() {
        // TODO(bassosimone): it's not optimal to use finalize
        if (submitter != null) {
            try {
                submitter.close();
            } catch (Exception exc) {
                // suppress
            }
            submitter = null;
        }
        if (makeSubmitterTask != null) {
            try {
                makeSubmitterTask.close();
            } catch (Exception exc) {
                // suppress
            }
            makeSubmitterTask = null;
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception exc) {
                // suppress
            }
        }
    }
}
