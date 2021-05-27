package org.openobservatory.ooniprobe.domain;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.common.JsonPrinter;
import org.openobservatory.ooniprobe.domain.callback.DomainCallback;
import org.openobservatory.ooniprobe.domain.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.factory.MeasurementFactory;
import org.openobservatory.ooniprobe.factory.ResponseFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import java.io.IOException;
import java.util.List;

import io.bloco.faker.Faker;
import okhttp3.OkHttpClient;
import okhttp3.Response;
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

    Faker faker = new Faker();
    OONIAPIClient apiClient = mock(OONIAPIClient.class);
    OkHttpClient httpClient = mock(OkHttpClient.class);
    JsonPrinter jsonPrinter = mock(JsonPrinter.class);

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


        DomainCallback<Boolean> callback = mock(DomainCallback.class);

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


        DomainCallback callback = mock(DomainCallback.class);

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


        DomainCallback callback = mock(DomainCallback.class);

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

    @Test
    public void downloadReportSuccessTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite()).getMeasurements().get(0);
        ApiMeasurement.Result result = new ApiMeasurement.Result();
        result.measurement_url = faker.internet.url();
        String successCallbackResponse = "{}";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ApiMeasurement> measurementCallback = mock(Call.class);
        okhttp3.Call httpCallback = mock(okhttp3.Call.class);

        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback =
                    (GetMeasurementsCallback) invocation.getArgument(0);
            getMeasurementsCallback.onSuccess(result);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        when(httpClient.newCall(any())).thenReturn(httpCallback);

        doAnswer(invocation -> {
            okhttp3.Callback newCallCallback =
                    (okhttp3.Callback) invocation.getArgument(0);

            Response response = ResponseFactory.successWithValue(
                    successCallbackResponse,
                    result.measurement_url
            );

            newCallCallback.onResponse(mock(okhttp3.Call.class), response);
            return null;
        }).when(httpCallback).enqueue(any(okhttp3.Callback.class));

        when(jsonPrinter.prettyText(successCallbackResponse)).thenReturn(successCallbackResponse);

        // Act
        manager.downloadReport(measurement, callback);

        // Assert
        verify(callback, times(1)).onSuccess(successCallbackResponse);
    }

    @Test
    public void downloadReportFailTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite()).getMeasurements().get(0);
        ApiMeasurement.Result result = new ApiMeasurement.Result();
        result.measurement_url = faker.internet.url();
        String failedCallbackResponse = "Something went wrong";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ApiMeasurement> measurementCallback = mock(Call.class);
        okhttp3.Call httpCallback = mock(okhttp3.Call.class);

        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback =
                    (GetMeasurementsCallback) invocation.getArgument(0);
            getMeasurementsCallback.onError(failedCallbackResponse);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        // Act
        manager.downloadReport(measurement, callback);

        // Assert
        verify(callback, times(1)).onError(failedCallbackResponse);
    }

    @Test
    public void downloadReportMeasurementFailTest() {
        // Arrange
        MeasurementsManager manager = build();
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite()).getMeasurements().get(0);
        ApiMeasurement.Result result = new ApiMeasurement.Result();
        result.measurement_url = faker.internet.url();
        String successCallbackResponse = "{}";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ApiMeasurement> measurementCallback = mock(Call.class);
        okhttp3.Call httpCallback = mock(okhttp3.Call.class);

        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback =
                    (GetMeasurementsCallback) invocation.getArgument(0);
            getMeasurementsCallback.onSuccess(result);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        when(httpClient.newCall(any())).thenReturn(httpCallback);

        doAnswer(invocation -> {
            okhttp3.Callback newCallCallback =
                    (okhttp3.Callback) invocation.getArgument(0);
            newCallCallback.onFailure(mock(okhttp3.Call.class), new IOException());
            return null;
        }).when(httpCallback).enqueue(any(okhttp3.Callback.class));

        when(jsonPrinter.prettyText(successCallbackResponse)).thenReturn(successCallbackResponse);

        // Act
        manager.downloadReport(measurement, callback);

        // Assert
        verify(callback, times(1)).onError(any());
    }

    public MeasurementsManager build() {
        return new MeasurementsManager(c, jsonPrinter, apiClient, httpClient);
    }

}