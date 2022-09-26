package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.client.callback.CheckReportIdCallback;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.factory.MeasurementFactory;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Experimental;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Tor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MeasurementTest extends RobolectricAbstractTest {

    @Override
    @After
    public void tearDown() {
        Result.deleteAll(c);
        super.tearDown();
    }

    @Test
    public void constructor2() {
        Result result = new Result();
        Measurement measurement = new Measurement(result, "test");

        assertEquals(result, measurement.result);
        assertEquals("test", measurement.test_name);
        assertTrue(System.currentTimeMillis() - measurement.start_time.getTime() < 100);
    }

    @Test
    public void constructor3() {
        Result result = new Result();
        Measurement measurement = new Measurement(result, "test", "report");

        assertEquals(result, measurement.result);
        assertEquals("test", measurement.test_name);
        assertEquals("report", measurement.report_id);
        assertTrue(System.currentTimeMillis() - measurement.start_time.getTime() < 100);
    }

    @Test
    public void selectUploadable() {
        Measurement notDone = new Measurement();
        notDone.is_done = false;
        notDone.save();
        Measurement notUploaded = new Measurement();
        notUploaded.is_done = true;
        notUploaded.is_uploaded = false;
        notUploaded.report_id = "1";
        notUploaded.save();
        Measurement noReportId = new Measurement();
        noReportId.is_done = true;
        noReportId.report_id = null;
        noReportId.save();

        List<Measurement> measurements = Measurement.selectUploadable().queryList();
        List<Integer> wantedIds = Arrays.asList(notUploaded.id, noReportId.id);

        assertEquals(2, measurements.size());
        assertTrue(wantedIds.contains(measurements.get(0).id));
        assertTrue(wantedIds.contains(measurements.get(1).id));
    }

    @Test
    public void selectUploaded() {
        Measurement uploaded = new Measurement();
        uploaded.is_uploaded = true;
        uploaded.report_id = null;
        uploaded.save();
        Measurement withReportId = new Measurement();
        withReportId.is_uploaded = false;
        withReportId.report_id = "1";
        withReportId.save();
        Measurement notUploaded = new Measurement();
        notUploaded.is_uploaded = false;
        notUploaded.report_id = null;
        notUploaded.save();

        List<Measurement> measurements = Measurement.selectUploaded().queryList();

        assertTrue(containsMeasurement(measurements, uploaded));
        assertTrue(containsMeasurement(measurements, withReportId));
        assertFalse(containsMeasurement(measurements, notUploaded));
    }

    @Test
    public void hasReport() {
        Measurement withoutReport = new Measurement();
        withoutReport.save();
        Measurement withReport = new Measurement();
        withReport.save();
        writeReport(withReport);

        assertFalse(Measurement.hasReport(c, queryById(withoutReport.id)));
        assertTrue(Measurement.hasReport(c, queryById(withReport.id)));
    }

    @Test
    public void withReport() {
        Measurement withoutReport = new Measurement();
        withoutReport.save();
        Measurement withReport = new Measurement();
        withReport.save();
        writeReport(withReport);

        List<Measurement> result = Measurement.withReport(c, queryByIds(withReport.id, withoutReport.id));

        assertEquals(1, result.size());
        assertEquals(withReport.id, result.get(0).id);
    }

    @Test
    public void selectUploadableWithResultId() {
        Result result1 = new Result();
        result1.save();
        Measurement uploadable1 = new Measurement();
        uploadable1.is_done = true;
        uploadable1.is_uploaded = false;
        uploadable1.result = result1;
        uploadable1.save();
        Result result2 = new Result();
        result2.save();
        Measurement uploadable2 = new Measurement();
        uploadable2.is_done = true;
        uploadable2.is_uploaded = false;
        uploadable2.result = result2;
        uploadable2.save();

        List<Measurement> result = Measurement.selectUploadableWithResultId(result1.id).queryList();

        assertEquals(1, result.size());
        assertEquals(uploadable1.id, result.get(0).id);
    }

    @Test
    public void manageReportFile() {
        Measurement measurement = new Measurement();
        measurement.test_name = "test";
        measurement.save();
        assertFalse(measurement.hasReportFile(c));

        writeReport(measurement);
        assertTrue(measurement.hasReportFile(c));

        measurement.deleteReportFile(c);
        assertFalse(measurement.hasReportFile(c));
    }

    @Test
    public void manageLogFile() {
        Result result = new Result();
        result.save();
        Measurement measurement = new Measurement();
        measurement.test_name = "test";
        measurement.result = result;
        measurement.save();
        assertFalse(measurement.hasLogFile(c));

        writeLog(measurement);
        assertTrue(measurement.hasLogFile(c));

        measurement.deleteLogFile(c);
        assertFalse(measurement.hasLogFile(c));
    }

    @Test
    public void deleteLogFileAfterAWeek() {
        Result result = new Result();
        result.save();
        Measurement measurement = new Measurement();
        measurement.test_name = "test";
        measurement.result = result;
        measurement.start_time = new Date();
        measurement.save();
        assertFalse(measurement.hasLogFile(c));

        writeLog(measurement);
        assertTrue(measurement.hasLogFile(c));

        measurement.deleteLogFileAfterAWeek(c);
        assertTrue(measurement.hasLogFile(c));

        measurement.start_time = new Date(System.currentTimeMillis() - PreferenceManager.DELETE_LOGS_DELAY - 1);
        measurement.deleteLogFileAfterAWeek(c);
        assertFalse(measurement.hasLogFile(c));
    }

    @Test
    public void getUrlString() {
        Measurement measurement = new Measurement();
        measurement.url = null;
        assertNull(measurement.getUrlString());

        measurement.url = new Url();
        measurement.url.url = "http://example.org";
        assertEquals(measurement.url.url, measurement.getUrlString());
    }

    @Test
    public void getStorageUsed() {
        assertTrue(Measurement.getStorageUsed(c) > 0);
    }

    @Test
    public void getMeasurementDir() {
        assertFalse(Measurement.getMeasurementDir(c).getAbsolutePath().isEmpty());
    }

    @Test
    public void getTest() {
        assertEquals(
                FacebookMessenger.class,
                MeasurementFactory.buildWithName(FacebookMessenger.NAME).getTest().getClass()
        );
        assertEquals(
                Telegram.class,
                MeasurementFactory.buildWithName(Telegram.NAME).getTest().getClass()
        );
        assertEquals(
                Whatsapp.class,
                MeasurementFactory.buildWithName(Whatsapp.NAME).getTest().getClass()
        );
        assertEquals(
                Signal.class,
                MeasurementFactory.buildWithName(Signal.NAME).getTest().getClass()
        );
        assertEquals(
                HttpHeaderFieldManipulation.class,
                MeasurementFactory.buildWithName(HttpHeaderFieldManipulation.NAME).getTest().getClass()
        );
        assertEquals(
                HttpInvalidRequestLine.class,
                MeasurementFactory.buildWithName(HttpInvalidRequestLine.NAME).getTest().getClass()
        );
        assertEquals(
                WebConnectivity.class,
                MeasurementFactory.buildWithName(WebConnectivity.NAME).getTest().getClass()
        );
        assertEquals(
                Ndt.class,
                MeasurementFactory.buildWithName(Ndt.NAME).getTest().getClass()
        );
        assertEquals(
                Dash.class,
                MeasurementFactory.buildWithName(Dash.NAME).getTest().getClass()
        );
        assertEquals(
                Psiphon.class,
                MeasurementFactory.buildWithName(Psiphon.NAME).getTest().getClass()
        );
        assertEquals(
                Tor.class,
                MeasurementFactory.buildWithName(Tor.NAME).getTest().getClass()
        );
        assertEquals(
                RiseupVPN.class,
                MeasurementFactory.buildWithName(RiseupVPN.NAME).getTest().getClass()
        );
        assertEquals(
                Experimental.class,
                MeasurementFactory.buildWithName("another").getTest().getClass()
        );
    }

    @Test
    public void getAndSetTestKeys() {
        Measurement measurement = new Measurement();
        assertNull(measurement.getTestKeys().accessible);

        TestKeys testKeys = new TestKeys();
        testKeys.accessible = "test";
        measurement.setTestKeys(testKeys);

        assertEquals("test", measurement.getTestKeys().accessible);
    }

    @Test
    public void isUploaded() {
        Measurement measurement = new Measurement();
        assertFalse(measurement.isUploaded());

        measurement.is_uploaded = true;
        assertFalse(measurement.isUploaded());

        measurement.report_id = "1";
        assertTrue(measurement.isUploaded());
    }


    @Test
    public void deleteUploadedJsons_onSuccess() throws InterruptedException {
        Application app = spy(a);
        PreferenceManager pm = mock(PreferenceManager.class);
        OONIAPIClient apiClient = mock(OONIAPIClient.class);
        when(app.getPreferenceManager()).thenReturn(pm);
        when(app.getApiClient()).thenReturn(apiClient);
        Call<ApiMeasurement> call = mock(Call.class);
        when(apiClient.checkReportId(any())).thenReturn(call);
        doAnswer(invocation -> {
            CheckReportIdCallback callback = (CheckReportIdCallback) invocation.getArgument(0);
            callback.onSuccess(true);
            return null;
        }).when(call).enqueue(any(Callback.class));

        Measurement measurement = MeasurementFactory.buildWithName("test");
        measurement.is_uploaded = true;
        measurement.report_id = "1";
        measurement.save();
        writeReport(measurement);
        assertTrue(measurement.hasReportFile(c));

        Measurement.deleteUploadedJsons(app);
        assertFalse(measurement.hasReportFile(c));
        verify(pm).setLastCalled();
    }

    @Test
    public void deleteUploadedJsons_onError() throws InterruptedException {
        Application app = spy(a);
        PreferenceManager pm = mock(PreferenceManager.class);
        OONIAPIClient apiClient = mock(OONIAPIClient.class);
        when(app.getPreferenceManager()).thenReturn(pm);
        when(app.getApiClient()).thenReturn(apiClient);
        Call<ApiMeasurement> call = mock(Call.class);
        when(apiClient.checkReportId(any())).thenReturn(call);
        doAnswer(invocation -> {
            CheckReportIdCallback callback = (CheckReportIdCallback) invocation.getArgument(0);
            callback.onError("error");
            return null;
        }).when(call).enqueue(any(Callback.class));

        Measurement measurement = new Measurement();
        measurement.is_uploaded = true;
        measurement.report_id = "1";
        measurement.save();
        writeReport(measurement);
        assertTrue(measurement.hasReportFile(c));

        Measurement.deleteUploadedJsons(app);
        Thread.sleep(100);
        assertTrue(measurement.hasReportFile(c));
        verify(pm).setLastCalled();
    }

    @Test
    public void deleteMeasurementWithReportId() {
        Measurement measurement = new Measurement();
        measurement.is_uploaded = true;
        measurement.report_id = "1";
        measurement.save();
        writeReport(measurement);
        assertTrue(measurement.hasReportFile(c));

        Measurement.deleteMeasurementWithReportId(c, measurement.report_id);
        assertFalse(measurement.hasReportFile(c));
    }

    @Test
    public void deleteOldLogs() {
        Result result = new Result();
        result.save();
        Measurement oldMeasurement = new Measurement();
        oldMeasurement.test_name = "test1";
        oldMeasurement.result = result;
        oldMeasurement.start_time = new Date(0);
        oldMeasurement.is_done = true;
        oldMeasurement.save();
        Measurement newMeasurement = new Measurement();
        newMeasurement.test_name = "test2";
        newMeasurement.result = result;
        newMeasurement.start_time = new Date();
        newMeasurement.is_done = true;
        newMeasurement.save();
        writeLog(oldMeasurement);
        writeLog(newMeasurement);
        assertTrue(oldMeasurement.hasLogFile(c));
        assertTrue(newMeasurement.hasLogFile(c));

        Measurement.deleteOldLogs(a);
        assertFalse(oldMeasurement.hasLogFile(c));
        assertTrue(newMeasurement.hasLogFile(c));
    }

    @Test
    public void setReRun() {
        Result result = new Result();
        result.save();
        Measurement measurement = new Measurement();
        measurement.test_name = "test";
        measurement.report_id = "1";
        measurement.result = result;
        measurement.start_time = new Date(0);
        measurement.save();
        writeReport(measurement);
        writeLog(measurement);
        assertTrue(measurement.hasReportFile(c));
        assertTrue(measurement.hasLogFile(c));

        measurement.setReRun(c);
        assertFalse(measurement.hasReportFile(c));
        assertFalse(measurement.hasLogFile(c));
        assertTrue(measurement.is_rerun);
    }

    // Helpers

    private boolean containsMeasurement(List<Measurement> measurements, Measurement measurement) {
        return Collections.binarySearch(measurements, measurement, (o1, o2) ->
                Integer.compare(o1.id, o2.id)
        ) >= 0;
    }

    private Where<Measurement> queryById(Integer id) {
        return queryByIds(id);
    }

    private Where<Measurement> queryByIds(Integer... ids) {
        return SQLite.select().from(Measurement.class).where(Measurement_Table.id.in(
                Arrays.asList(ids)
        ));
    }

    private void writeReport(Measurement measurement) {
        writeFile(
                Measurement.getReportFile(c, measurement.id, measurement.test_name)
        );
    }

    private void writeLog(Measurement measurement) {
        writeFile(
                Measurement.getLogFile(c, measurement.result.id, measurement.test_name)
        );
    }

    private void writeFile(File file) {
        try {
            FileUtils.writeStringToFile(
                    file,
                    "test",
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
