package org.openobservatory.ooniprobe.engine;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.engine.OONILogger;
import org.openobservatory.engine.OONIMKTask;
import org.openobservatory.engine.OONIMKTaskConfig;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONISessionConfig;
import org.openobservatory.ooniprobe.factory.EventResultFactory;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.test.EngineInterface;

import java.util.LinkedList;
import java.util.Queue;

import static org.openobservatory.ooniprobe.TestApplicationProvider.app;

public class TestEngineInterface extends EngineInterface {

    public TestOONIMKTask experimentTask = new TestOONIMKTask();
    private final OONISession session;

    public boolean isTaskDone = false;

    private final Gson gson = app().getGson();
    private Queue<EventResult> queueEvents = new LinkedList<>();
    private boolean taskInterrupted = false;

    public TestEngineInterface(OONISession session) {
        this.session = session;
    }

    @Override
    public String newUUID4() {
        return "UUID4";
    }

    @Override
    public OONIMKTask startExperimentTask(OONIMKTaskConfig settings) throws Exception {
        return experimentTask;
    }

    @Override
    public OONISession newSession(OONISessionConfig config) {
        return session;
    }

    @Override
    public OONISessionConfig getDefaultSessionConfig(Context ctx, String softwareName, String softwareVersion, OONILogger logger, String proxy) throws Exception {
        return super.getDefaultSessionConfig(ctx, softwareName, softwareVersion, logger, proxy);
    }

    public void sendNextEvent(EventResult nextEvent) {
        queueEvents.add(nextEvent);
    }

    public class TestOONIMKTask implements OONIMKTask {

        @Override
        public boolean isDone() {
            return isTaskDone;
        }

        @Override
        public String waitForNextEvent() {
            EventResult currentEvent = queueEvents.poll();

            if (currentEvent == null) {
                isTaskDone = true;
                return gson.toJson(EventResultFactory.buildEnded());
            }

            if (currentEvent.key.equals("task_terminated")) {
                isTaskDone = true;
            }

            return gson.toJson(currentEvent);
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