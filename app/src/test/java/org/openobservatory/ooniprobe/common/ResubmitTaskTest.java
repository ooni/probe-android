package org.openobservatory.ooniprobe.common;

import android.content.Intent;
import android.content.res.Resources;

import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.TestApplicationProvider;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.domain.MeasurementsManager;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.EngineInterface;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SmallTest
public class ResubmitTaskTest extends RobolectricAbstractTest {

    OONISession ooniSessionMock = mock(OONISession.class);
    MeasurementsManager managerMock = mock(MeasurementsManager.class);

    // Engine && UseCase
    EngineInterface mockedEngine = new TestEngineInterface(ooniSessionMock);

    Resources resources = TestApplicationProvider.app().getResources();

    @Override
    public void setUp() {
        super.setUp();
        DatabaseUtils.resetDatabase();
        EngineProvider.engineInterface = mockedEngine;
    }

    @Test
    public void notUploadedMeasurementsTest() {
        // Arrange
        Result testResult = ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(resources), 5, 0, false);
        ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(resources), 5, 0, false);
        ResubmitTask<ResultDetailActivity> resubmitTask = build(testResult.id);

        when(managerMock.reSubmit(any(), any())).thenReturn(true);

        // Act
        resubmitTask.execute(null, null);
        idleTaskUntilFinished(resubmitTask);

        // Assert
        assertEquals(10, resubmitTask.totUploads.intValue());
        assertEquals(0, resubmitTask.errors.intValue());
    }

    @Test
    public void notUploadedByResultIdMeasurementsTest() {
        // Arrange
        Result testResult = ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(resources), 5, 0, false);
        ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(resources), 5, 0, false);
        ResubmitTask<ResultDetailActivity> resubmitTask = build(testResult.id);

        when(managerMock.reSubmit(any(), any())).thenReturn(true);

        // Act
        resubmitTask.execute(testResult.id, null);
        idleTaskUntilFinished(resubmitTask);

        // Assert
        assertEquals(5, resubmitTask.totUploads.intValue());
        assertEquals(0, resubmitTask.errors.intValue());
    }

    @Test
    public void notUploadedByMeasurementIdMeasurementsTest() {
        // Arrange
        Result testResult = ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(resources), 5, 0, false);
        ResubmitTask<ResultDetailActivity> resubmitTask = build(testResult.id);

        when(managerMock.reSubmit(any(), any())).thenReturn(true);

        // Act
        resubmitTask.execute(null, testResult.getMeasurements().get(0).id);
        idleTaskUntilFinished(resubmitTask);

        // Assert
        assertEquals(1, resubmitTask.totUploads.intValue());
        assertEquals(0, resubmitTask.errors.intValue());
    }

    public ResubmitTask<ResultDetailActivity> build(int testResultId) {
        Intent intent = new Intent(c, ResultDetailActivity.class).putExtra("id", testResultId);
        ResultDetailActivity activity = buildActivity(ResultDetailActivity.class, intent);
        ResubmitTask<ResultDetailActivity> resubmitTask = new ResubmitTask<>(activity, "");
        resubmitTask.d.measurementsManager = managerMock;
        resubmitTask.publishProgress = false;
        return resubmitTask;
    }
}
