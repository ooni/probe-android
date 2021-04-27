package org.openobservatory.ooniprobe.engine;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.engine.OONILogger;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.engine.OONIMKTaskConfig;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONISessionConfig;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.test.EngineInterface;

import static org.openobservatory.ooniprobe.TestApplicationProvider.app;

public class TestEngineInterface extends EngineInterface {

    public final TestOONIMKTask experimentTask = new TestOONIMKTask();

    public boolean isTaskDone = false;

    private final Gson gson = app().getGson();
    private EventResult nextEvent = null;
    private boolean taskInterrupted = false;

    @Override
    public OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws Exception {
        return experimentTask;
    }

    @Override
    public String resolveProbeCC(Context ctx, String softwareName, String softwareVersion, long timeout) throws Exception {
        return super.resolveProbeCC(ctx, softwareName, softwareVersion, timeout);
    }

    @Override
    public OONISession newSession(OONISessionConfig config) throws Exception {
        return super.newSession(config);
    }

    @Override
    public OONISessionConfig getDefaultSessionConfig(Context ctx, String softwareName, String softwareVersion, OONILogger logger) throws Exception {
        return super.getDefaultSessionConfig(ctx, softwareName, softwareVersion, logger);
    }

    public void sendNextEvent(EventResult nextEvent) {
        this.nextEvent = nextEvent;
    }

    public class TestOONIMKTask implements OONIMKTask {

        @Override
        public boolean isDone() {
            return isTaskDone;
        }

        @Override
        public String waitForNextEvent() {
            while (nextEvent == null && !taskInterrupted) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String jsonResult = gson.toJson(nextEvent);
            nextEvent = null;
            return jsonResult;
        }

        @Override
        public boolean canInterrupt() {
            return true;
        }

        @Override
        public void interrupt() {
            taskInterrupted = true;
        }
    }
}
