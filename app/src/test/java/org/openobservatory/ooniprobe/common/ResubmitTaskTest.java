package org.openobservatory.ooniprobe.common;

import android.content.Intent;

import androidx.test.filters.SmallTest;

import org.junit.Ignore;
import org.junit.Test;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.factory.MeasurementFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.EngineInterface;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@SmallTest
public class ResubmitTaskTest extends RobolectricAbstractTest {

    OONISession ooniSessionMock = mock(OONISession.class);

    // Engine && UseCase
    EngineInterface mockedEngine = new TestEngineInterface(ooniSessionMock);

    @Override
    public void setUp() {
        super.setUp();
        DatabaseUtils.resetDatabase();
        EngineProvider.engineInterface = mockedEngine;
    }

    @Test
    @Ignore("Test no finished, task will suffer further alterations")
    public void submitNotUploadedMeasurementsTest() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new WebsitesSuite(), 5, 0, false);
        MeasurementFactory.addEntryFiles(c, testResult.getMeasurements(), false);
        testResult.save();

        Intent intent = new Intent(c, ResultDetailActivity.class).putExtra("id", testResult.id);
        ResultDetailActivity activity = buildActivity(ResultDetailActivity.class, intent);
        ResubmitTask<ResultDetailActivity> resubmitTask = new ResubmitTask<>(activity, "");

        // Act
        resubmitTask.execute(testResult.id, null);
        idleTaskUntilFinished(resubmitTask);

        // Assert
        assertEquals(5, resubmitTask.totUploads.intValue());
        assertEquals(0, resubmitTask.errors.intValue());
    }

    @Test
    public void standardTimeout() {
        assertEquals(ResubmitTask.getTimeout(2000), 11);
    }

    @Test
    public void zeroTimeout() {
        assertEquals(ResubmitTask.getTimeout(0), 10);
    }

    @Test
    public void maxTimeout() {
        assertEquals(ResubmitTask.getTimeout(Long.MAX_VALUE), Long.MAX_VALUE / 2000 + 10);
    }
}
