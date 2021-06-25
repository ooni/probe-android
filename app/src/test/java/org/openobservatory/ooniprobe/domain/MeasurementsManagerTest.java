package org.openobservatory.ooniprobe.domain;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.factory.MeasurementFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MeasurementsManagerTest extends RobolectricAbstractTest {

    OONIAPIClient apiClient = mock(OONIAPIClient.class);

    @Override
    public void setUp() {
        super.setUp();
        DatabaseUtils.resetDatabase();
    }

    @Test
    public void testGetMeasurement() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        // Act
        Measurement value = manager.get(measurement.id);

        // Assert
        assertNotNull(value);
        assertEquals(measurement.id, value.id);
        assertEquals(measurement.test_name, value.test_name);
        assertEquals(measurement.test_keys, value.test_keys);
    }

    @Test
    public void testCanUpload() {
        // Arrange
        MeasurementsManager manager = build();
        List<Measurement> measurements = ResultFactory.createAndSave(new WebsitesSuite(), 1, 0, false)
                .getMeasurements();

        MeasurementFactory.addEntryFiles(c, measurements, false);

        // Act
        boolean value = manager.canUpload(measurements.get(0));

        // Assert
        assertTrue(value);
    }

    @Test
    public void testExplorerUrl() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new PerformanceSuite())
                .getMeasurements()
                .get(0);

        String url = "https://explorer.ooni.io/measurement/"
                + measurement.report_id;

        // Act
        String value = manager.getExplorerUrl(measurement);

        // Assert
        assertEquals(url, value);
    }

    @Test
    public void testWebConnectivityExplorer() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        String url = "https://explorer.ooni.io/measurement/"
                + measurement.report_id
                + "?input="
                + measurement.url.url;

        // Act
        String value = manager.getExplorerUrl(measurement);

        // Assert
        assertEquals(url, value);
    }

    @Test
    public void testCanNotUpload() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        // Act
        boolean value = manager.canUpload(measurement);

        // Assert
        assertFalse(value);
    }

    @Test
    public void checkAndDeleteReportTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        MeasurementFactory.addEntryFile(c, measurement.report_id, measurement, true);

        Call<ApiMeasurement> call = mock(Call.class);
        when(apiClient.checkReportId(measurement.report_id)).thenReturn(call);

        doAnswer(invocation -> {
            CheckReportIdCallback callback =
                    (CheckReportIdCallback) invocation.getArgument(0);
            callback.onSuccess(true);
            return null;
        }).when(call).enqueue(any(Callback.class));


        CheckReportIdCallback callback = mock(CheckReportIdCallback.class);

        // Act
        manager.checkReportAndDeleteIt(measurement, callback);

        // Assert
        verify(callback).onSuccess(true);
    }

    @Test
    public void checkAndDeleteReportNotFoundTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        MeasurementFactory.addEntryFile(c, measurement.report_id, measurement, true);

        Call<ApiMeasurement> call = mock(Call.class);
        when(apiClient.checkReportId(measurement.report_id)).thenReturn(call);

        doAnswer(invocation -> {
            CheckReportIdCallback callback =
                    (CheckReportIdCallback) invocation.getArgument(0);
            callback.onSuccess(false);
            return null;
        }).when(call).enqueue(any(Callback.class));


        CheckReportIdCallback callback = mock(CheckReportIdCallback.class);

        // Act
        manager.checkReportAndDeleteIt(measurement, callback);

        // Assert
        verify(callback).onSuccess(false);
    }

    @Test
    public void checkAndDeleteReportFailTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);

        MeasurementFactory.addEntryFile(c, measurement.report_id, measurement, true);

        String errorMessage = "ups";

        Call<ApiMeasurement> call = mock(Call.class);
        when(apiClient.checkReportId(measurement.report_id)).thenReturn(call);

        doAnswer(invocation -> {
            CheckReportIdCallback callback =
                    (CheckReportIdCallback) invocation.getArgument(0);
            callback.onError(errorMessage);
            return null;
        }).when(call).enqueue(any(Callback.class));


        CheckReportIdCallback callback = mock(CheckReportIdCallback.class);

        // Act
        manager.checkReportAndDeleteIt(measurement, callback);

        // Assert
        verify(callback, times(1)).onError(errorMessage);
    }

    @Test
    public void testUploadableReports() {
        // Arrange
        MeasurementsManager manager = build();
        Result testResult = ResultFactory.createAndSave(new WebsitesSuite(), 5, 0, false);
        MeasurementFactory.addEntryFiles(c, testResult.getMeasurements(), false);
        testResult.save();

        // Act
        boolean hasReports = manager.hasUploadables();

        // Assert
        assertTrue(hasReports);
    }

    @Test
    public void testNoUploadableReports() {
        // Arrange
        MeasurementsManager manager = build();
        ResultFactory.createAndSave(new WebsitesSuite());

        // Act
        boolean hasReports = manager.hasUploadables();

        // Assert
        assertFalse(hasReports);
    }

    @Test
    public void hasReportIdTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite()).getMeasurements().get(0);

        // Act
        boolean hasReportId = manager.hasReportId(measurement);

        // Assert
        assertTrue(hasReportId);
    }

    public MeasurementsManager build() {
        return new MeasurementsManager(c, apiClient);
    }

}