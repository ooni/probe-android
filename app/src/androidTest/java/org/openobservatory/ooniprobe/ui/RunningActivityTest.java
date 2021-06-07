package org.openobservatory.ooniprobe.ui;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import com.schibsted.spain.barista.rule.flaky.AllowFlaky;
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.model.jsonresult.EventResult;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.openobservatory.ooniprobe.testing.ActivityAssertions.assertCurrentActivity;
import static org.openobservatory.ooniprobe.testing.ActivityAssertions.waitForCurrentActivity;

@RunWith(AndroidJUnit4.class)
public class RunningActivityTest extends AbstractTest {

    private ActivityScenario<RunningActivity> scenario;
    private final TestEngineInterface testEngine = new TestEngineInterface();

    private final FlakyTestRule flakyRule = new FlakyTestRule();
    private final ServiceTestRule serviceRule = new ServiceTestRule();

    @Rule
    public RuleChain chain = RuleChain.outerRule(flakyRule)
            .around(serviceRule);

    @Before
    public void setUp() {
        DatabaseUtils.resetDatabase();
        EngineProvider.engineInterface = testEngine;
    }

    @After
    public void tearDown() {
        scenario.close();
        serviceRule.unbindService();
    }

    @Test
    @AllowFlaky()
    public void startAndDone() {
        launch();
        assertCurrentActivity(RunningActivity.class);

        EventResult start = new EventResult();
        start.key = "status.started";
        testEngine.sendNextEvent(start);

        EventResult done = new EventResult();
        done.key = "status.end";
        testEngine.sendNextEvent(done);

        testEngine.isTaskDone = true;

        waitForCurrentActivity(MainActivity.class);
    }

    private void launch() {
        startRunTestService();
        scenario = ActivityScenario.launch(RunningActivity.class);
    }

    private void startRunTestService() {
        try {
            serviceRule.startService(
                    new Intent(c, RunTestService.class)
                            .putExtra("testSuites", new ArrayList<AbstractSuite>() {{
                                add(new InstantMessagingSuite());
                            }})
            );
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
