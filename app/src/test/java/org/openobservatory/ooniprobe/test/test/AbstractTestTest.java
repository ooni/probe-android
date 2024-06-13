package org.openobservatory.ooniprobe.test.test;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.AppLogger;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.factory.EventResultFactory;
import org.openobservatory.ooniprobe.factory.JsonResultFactory;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.factory.UrlFactory;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.model.settings.Settings;
import org.openobservatory.ooniprobe.test.EngineProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.bloco.faker.Faker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openobservatory.ooniprobe.test.test.AbstractTest.TestCallback;

public class AbstractTestTest extends RobolectricAbstractTest {

    Faker faker = new Faker();

    static final String REPORT_ID = "1000_00001";
    static final int MEASUREMENT_ID = 1;

    PreferenceManager mockPreferenceManager = mock(PreferenceManager.class);
    TestCallback mockedCallback = mock(TestCallback.class);
    OONISession mockedSession = mock(OONISession.class);
    Settings mockedSettings = mock(Settings.class);

    AppLogger appLogger = mock(AppLogger.class);
    Gson gson = new Gson();

    TestEngineInterface testEngine = new TestEngineInterface(mockedSession);

    @Override
    public void setUp() {
        super.setUp();
        EngineProvider.engineInterface = testEngine;

        try {
            when(mockedSettings.toExperimentSettings(any(), any())).thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testStartFinish() {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager, appLogger, gson, mockedSettings, result, 1, mockedCallback);

        // Assert
        verify(mockedCallback, times(1)).onStart(c.getString(R.string.Test_WebConnectivity_Fullname));
        verify(mockedCallback, times(1)).onProgress(100);
    }

    @Test
    public void testCreateNewMeasurement() {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        Url ulr = UrlFactory.createAndSave();

        JsonResult jsonResult = JsonResultFactory.build(test, true);

        String measurementName = test.getName();
        String measurementUrl = ulr.url;
        String reportId = REPORT_ID;
        String jsonResultString = gson.toJson(jsonResult);
        double measurementDownload = 10;
        double measurementUpload = 15;

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildCreateReport(reportId));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementStart(MEASUREMENT_ID, measurementUrl));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementEntry(MEASUREMENT_ID, jsonResultString));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementUpload(MEASUREMENT_ID, false));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementDone(MEASUREMENT_ID));
        testEngine.sendNextEvent(EventResultFactory.buildDataUsage(measurementDownload, measurementUpload));
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);

        // Assert
        verify(mockedCallback, times(1)).onStart(c.getString(R.string.Test_WebConnectivity_Fullname));
        verify(mockedCallback, times(1)).onProgress(100);

        Result updatedResult = SQLite.select().from(Result.class).where(Result_Table.id.eq(result.id)).querySingle();
        assertNotNull(updatedResult);

        Measurement measurement = updatedResult.getMeasurements().get(0);
        assertNotNull(measurement);

        assertTrue(updatedResult.is_done);
        assertEquals(updatedResult.data_usage_down, measurementDownload, 0);
        assertEquals(updatedResult.data_usage_up, measurementUpload, 0);
        assertEquals(updatedResult.start_time, jsonResult.test_start_time);

        assertEquals(measurement.test_name, measurementName);
        assertEquals(measurement.url.url, measurementUrl);
        assertEquals(measurement.start_time, jsonResult.measurement_start_time);
        assertEquals(measurement.runtime, jsonResult.test_runtime, 0);
        assertEquals(measurement.report_id, reportId);
        assertTrue(measurement.is_uploaded);
        assertFalse(measurement.is_anomaly);
    }

    @Test
    public void testNetworkEvent() {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        String networkName = faker.internet.domainName();
        String ip = faker.internet.ipV4Address();
        String asn = "asn";
        String countryCode = faker.address.countryCode();

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildIpLookup(networkName, ip, asn, countryCode));
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Result updatedResult = SQLite.select().from(Result.class).where(Result_Table.id.eq(result.id)).querySingle();

        // Assert
        assertNotNull(updatedResult);
        assertEquals(networkName, updatedResult.network.network_name);
        assertEquals(ip, updatedResult.network.ip);
        assertEquals(asn, updatedResult.network.asn);
        assertEquals(countryCode, updatedResult.network.country_code);
        assertEquals(ReachabilityManager.MOBILE, updatedResult.network.network_type);
    }

    @Test
    public void testLogEvent() throws IOException {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        String message = "Ipsum";

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildLog(message));
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);

        File logFile = Measurement.getLogFile(c, result.id, test.getName());
        String content = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8).replace("\n", "");

        // Assert
        verify(mockedCallback, times(1)).onLog(message);
        assertEquals(message, content);
    }

    @Test
    public void testProgressEvent() throws IOException {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        String message = "Ipsum";

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildProgress(0D, message));
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);

        File logFile = Measurement.getLogFile(c, result.id, test.getName());
        String content = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8).replace("\n", "");

        // Assert
        verify(mockedCallback, times(1)).onLog(message);
        verify(mockedCallback, times(2)).onProgress(100);
        assertEquals(message, content);
    }

    @Test
    public void testResolverFailureEvent() {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        String message = "Ipsum";

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.resolverFailure(message));
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Result updatedResult = SQLite.select().from(Result.class).where(Result_Table.id.eq(result.id)).querySingle();

        // Assert
        assertNotNull(updatedResult);
        assertEquals(message, updatedResult.failure_msg);
    }

    @Test
    public void testTaskInterrupt() {
        // Arrange
        AbstractTest test = new WebConnectivity();
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        TestEngineInterface.TestOONIMKTask task = mock(TestEngineInterface.TestOONIMKTask.class);
        testEngine.experimentTask = task;

        when(task.canInterrupt()).thenReturn(true);
        when(task.isDone()).thenReturn(true);

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildEnded());

        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);

        boolean value = test.canInterrupt();
        test.interrupt();

        // Assert
        assertTrue(value);
        verify(task, times(1)).interrupt();
    }

    @Test
    public void testNdt() {
        // Arrange
        Ndt test = new Ndt();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);

        TestKeys testKeys = updatedMeasurement.getTestKeys();

        assertEquals(testKeys.server_name, "lis02");
        assertEquals(testKeys.server_country, "PT");
    }

    @Test
    public void testTor() {
        // Arrange
        Tor test = new Tor();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testTorFail() {
        // Arrange
        Tor test = new Tor();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testRiseupVpn() {
        // Arrange
        AbstractTest test = new RiseupVPN();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testRiseupVpnFail() {
        // Arrange
        AbstractTest test = new RiseupVPN();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testWhatsapp() {
        // Arrange
        AbstractTest test = new Whatsapp();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testWhatsappFail() {
        // Arrange
        AbstractTest test = new Whatsapp();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testTelegram() {
        // Arrange
        AbstractTest test = new Telegram();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testTelegramFail() {
        // Arrange
        AbstractTest test = new Telegram();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testFacebookMessenger() {
        // Arrange
        AbstractTest test = new FacebookMessenger();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testFacebookMessengerFail() {
        // Arrange
        AbstractTest test = new FacebookMessenger();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testHttpHeaderFieldManipulation() {
        // Arrange
        AbstractTest test = new HttpHeaderFieldManipulation();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testHttpHeaderFieldManipulationFail() {
        // Arrange
        AbstractTest test = new HttpHeaderFieldManipulation();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testHttpInvalidRequestLine() {
        // Arrange
        AbstractTest test = new HttpInvalidRequestLine();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testHttpInvalidRequestLineFail() {
        // Arrange
        AbstractTest test = new HttpInvalidRequestLine();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testSignal() {
        // Arrange
        AbstractTest test = new Signal();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testSignalFail() {
        // Arrange
        AbstractTest test = new Signal();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testDash() {
        // Arrange
        AbstractTest test = new Dash();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
        assertFalse(updatedMeasurement.is_failed);
    }

    @Test
    public void testDashFail() {
        // Arrange
        AbstractTest test = new Dash();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_failed);
    }

    @Test
    public void testPsiphon() {
        // Arrange
        AbstractTest test = new Psiphon();
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testPsiphonFail() {
        // Arrange
        AbstractTest test = new Psiphon();
        Result result = setupTestRun(test, false);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testExperimental() {
        // Arrange
        AbstractTest test = new Experimental("");
        Result result = setupTestRun(test, true);

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertFalse(updatedMeasurement.is_anomaly);
    }

    @Test
    public void testExperimentalFail() {
        // Arrange
        AbstractTest test = new Experimental("");
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildCreateReport(REPORT_ID));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementStart(MEASUREMENT_ID, UrlFactory.createAndSave().url));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementEntry(MEASUREMENT_ID, ""));

        // Act
        test.run(c, mockPreferenceManager,appLogger , gson, mockedSettings, result, 1, mockedCallback);
        Measurement updatedMeasurement = SQLite.select().from(Measurement.class).where(Measurement_Table.report_id.eq(REPORT_ID)).querySingle();

        // Assert
        assertNotNull(updatedMeasurement);
        assertTrue(updatedMeasurement.is_failed);
    }

    private Result setupTestRun(AbstractTest test, boolean success) {
        Result result = ResultFactory.build(OONITests.WEBSITES.toOONIDescriptor(c), true, false);
        result.save();

        JsonResult jsonResult = JsonResultFactory.build(test, success);
        String jsonResultString = gson.toJson(jsonResult);

        // Act
        testEngine.sendNextEvent(EventResultFactory.buildStarted());
        testEngine.sendNextEvent(EventResultFactory.buildCreateReport(REPORT_ID));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementStart(MEASUREMENT_ID, UrlFactory.createAndSave().url));
        testEngine.sendNextEvent(EventResultFactory.buildMeasurementEntry(MEASUREMENT_ID, jsonResultString));

        return result;
    }
}