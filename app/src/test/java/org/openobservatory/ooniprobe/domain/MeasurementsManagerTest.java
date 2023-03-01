package org.openobservatory.ooniprobe.domain;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONISubmitResults;
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
import org.openobservatory.ooniprobe.model.database.Measurement_Table;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.bloco.faker.Faker;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    MeasurementsManager manager;

    @Override
    public void setUp() {
        super.setUp();
        manager = new MeasurementsManager(c, jsonPrinter, apiClient, httpClient);
    }

    @Test
    public void testGetMeasurement() {
        // Arrange
        Measurement measurement = buildMeasurement();

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
        List<Measurement> measurements =
                ResultFactory.createAndSave(new WebsitesSuite(), 1, 0, false)
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
        Measurement measurement = buildMeasurement();
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
    public void testWebConnectivityExplorer() {
        // Arrange
        Measurement measurement = buildMeasurement();

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
        Measurement measurement = buildMeasurement();

        // Act
        boolean value = manager.canUpload(measurement);

        // Assert
        assertFalse(value);
    }

    @Test
    public void checkAndDeleteReportTest() {
        // Arrange
        Measurement measurement = buildMeasurement();

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
        Measurement measurement = buildMeasurement();

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
        Measurement measurement = buildMeasurement();

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
        ResultFactory.createAndSave(new WebsitesSuite());

        // Act
        boolean hasReports = manager.hasUploadables();

        // Assert
        assertFalse(hasReports);
    }

    @Test
    public void hasReportIdTest() {
        // Arrange
        Measurement measurement = buildMeasurement();

        // Act
        boolean hasReportId = manager.hasReportId(measurement);

        // Assert
        assertTrue(hasReportId);
    }

    @Test
    public void getReadableLog() throws IOException {
        // Arrange
        Measurement measurement = buildMeasurement();
        File file = Measurement.getLogFile(c, measurement.result.id, measurement.test_name);
        FileUtils.writeStringToFile(file, "test", StandardCharsets.UTF_8);

        // Act
        String output = manager.getReadableLog(measurement);

        // Assert
        assertEquals("test", output);
    }

    @Test
    public void getReadableEntry() throws IOException {
        // Arrange
        Measurement measurement =
                ResultFactory.createAndSaveWithEntryFiles(c, new WebsitesSuite(), 1, 0, false)
                        .getMeasurements().get(0);
        when(jsonPrinter.prettyText("test")).thenReturn("pretty test");

        // Act
        String output = manager.getReadableEntry(measurement);

        // Assert
        verify(jsonPrinter).prettyText("test");
        assertEquals("pretty test", output);
    }

    @Test
    public void downloadReportSuccessTest() {
        // Arrange
        Measurement measurement = buildMeasurement();
        String result = "{}";
        String measurement_url = faker.internet.url();
        String successCallbackResponse = "{}";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ResponseBody> measurementCallback = mock(Call.class);
        okhttp3.Call httpCallback = mock(okhttp3.Call.class);

        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback = invocation.getArgument(0);
            getMeasurementsCallback.onSuccess(result);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        when(httpClient.newCall(any())).thenReturn(httpCallback);

        doAnswer(invocation -> {
            okhttp3.Callback newCallCallback =
                    (okhttp3.Callback) invocation.getArgument(0);

            Response response = ResponseFactory.successWithValue(
                    successCallbackResponse,
                    measurement_url
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
        Measurement measurement = ResultFactory.createAndSave(new WebsitesSuite()).getMeasurements().get(0);
        ApiMeasurement.Result result = new ApiMeasurement.Result();
        result.measurement_url = faker.internet.url();
        String failedCallbackResponse = "Something went wrong";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ResponseBody> measurementCallback = mock(Call.class);
        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback = invocation.getArgument(0);
            getMeasurementsCallback.onError(failedCallbackResponse);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        // Act
        manager.downloadReport(measurement, callback);

        // Assert
        verify(callback, times(1)).onError(failedCallbackResponse);
    }

    // @Test
    // TODO(aanorbel): remove with resolution of https://github.com/ooni/probe/issues/2146
    public void downloadReportMeasurementFailTest() {
        // Arrange
        Measurement measurement = buildMeasurement();
        String result = null;
        String successCallbackResponse = "{}";

        DomainCallback<String> callback = mock(DomainCallback.class);
        Call<ResponseBody> measurementCallback = mock(Call.class);
        okhttp3.Call httpCallback = mock(okhttp3.Call.class);

        when(apiClient.getMeasurement(measurement.report_id, measurement.getUrlString()))
                .thenReturn(measurementCallback);

        doAnswer(invocation -> {
            GetMeasurementsCallback getMeasurementsCallback = invocation.getArgument(0);
            getMeasurementsCallback.onSuccess(result);
            return null;
        }).when(measurementCallback).enqueue(any(Callback.class));

        when(httpClient.newCall(any())).thenReturn(httpCallback);

        doAnswer(invocation -> {
            okhttp3.Callback newCallCallback = invocation.getArgument(0);
            newCallCallback.onFailure(mock(okhttp3.Call.class), new IOException());
            return null;
        }).when(httpCallback).enqueue(any(okhttp3.Callback.class));

        when(jsonPrinter.prettyText(successCallbackResponse)).thenReturn(successCallbackResponse);

        // Act
        manager.downloadReport(measurement, callback);

        // Assert
        verify(callback, times(1)).onError(any());
    }

    @Test
    public void standardTimeoutTest() {
        // Assert
        assertEquals(manager.getTimeout(2000), 11);
    }

    @Test
    public void zeroTimeoutTest() {
        // Assert
        assertEquals(manager.getTimeout(0), 10);
    }

    @Test
    public void maxTimeoutTest() {
        // Assert
        assertEquals(manager.getTimeout(Long.MAX_VALUE), Long.MAX_VALUE / 2000 + 10);
    }

    @Test
    public void reSubmitTest() {
        try {

            // Arrange
            String newReportId = "abc";
            String fileContent = "{}";
            Measurement measurement = ResultFactory.createAndSaveWithEntryFiles(
                    c,
                    new WebsitesSuite(),
                    5,
                    0,
                    false
            ).getMeasurements().get(0);

            OONIContext ooniContext = mock(OONIContext.class);
            OONISession ooniSession = mock(OONISession.class);
            OONISubmitResults submitResults = mock(OONISubmitResults.class);

            when(ooniSession.submit(any(), any())).thenReturn(submitResults);
            when(ooniSession.newContextWithTimeout(anyLong())).thenReturn(ooniContext);

            when(submitResults.getUpdatedMeasurement()).thenReturn(fileContent);
            when(submitResults.getUpdatedReportID()).thenReturn(newReportId);

            // Act
            boolean value = manager.reSubmit(measurement, ooniSession);
            Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(newReportId)).querySingle();
            File updatedFile = Measurement.getReportFile(c, updatedMeasurement.id, updatedMeasurement.test_name);
            String updatedFileContent = FileUtils.readFileToString(updatedFile, StandardCharsets.UTF_8);

            // Assert
            assertTrue(value);
            assertNotNull(updatedMeasurement);
            assertNotNull(updatedFile);
            assertEquals(measurement.test_name, updatedMeasurement.test_name);
            assertEquals(measurement.url.url, updatedMeasurement.url.url);
            assertEquals(fileContent, updatedFileContent);
            assertTrue(updatedMeasurement.is_uploaded);
            assertFalse(updatedMeasurement.is_upload_failed);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private Measurement buildMeasurement() {
        return ResultFactory.createAndSave(new WebsitesSuite())
                .getMeasurements()
                .get(0);
    }
}
