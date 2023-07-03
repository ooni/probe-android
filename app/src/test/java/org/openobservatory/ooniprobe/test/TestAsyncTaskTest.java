package org.openobservatory.ooniprobe.test;

import android.app.Notification;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

import io.bloco.faker.Faker;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestAsyncTaskTest extends RobolectricAbstractTest {

    private final RunTestService runService = mock(RunTestService.class);
    private final OONISession ooniSessionMock = mock(OONISession.class);
    private final TestEngineInterface mockedEngine = new TestEngineInterface(ooniSessionMock);
    private final NotificationCompat.Builder builder = mock(NotificationCompat.Builder.class);
    private final NotificationManagerCompat notificationManager = mock(NotificationManagerCompat.class);

    @Override
    @Before
    public void setUp() {
        super.setUp();
        EngineProvider.engineInterface = mockedEngine;

        runService.builder = builder;
        when(builder.setContentText(any())).thenReturn(builder);
        when(builder.setProgress(anyInt(), anyInt(), anyBoolean())).thenReturn(mock(NotificationCompat.Builder.class));
        when(builder.build()).thenReturn(mock(Notification.class));
        runService.notificationManager = notificationManager;
    }

    @Test
    public void storesTestSuitInDb() {
        // Arrange
        ArrayList<AbstractSuite> suiteList = new ArrayList<>();
        AbstractSuite mockedSuite = mock(WebsitesSuite.class);
        suiteList.add(mockedSuite);
        TestAsyncTask task = new TestAsyncTask(a, suiteList);
        Result testResult = ResultFactory.build(new WebsitesSuite(), true, true);

        when(mockedSuite.getTestList(any())).thenReturn(new AbstractTest[0]);

        when(mockedSuite.getResult()).thenReturn(testResult);

        // Act
        mockedEngine.isTaskDone = true;
        task.execute();
        idleTaskUntilFinished(task);

        Result databaseResult = DatabaseUtils.findResult(testResult.id);

        // Assert
        assertNotNull(databaseResult);
        assertEquals(testResult.id, databaseResult.id);
        assertFalse(databaseResult.is_viewed);
    }

    @Test
    public void downloadsWebSuitUrls() throws Exception {
        // Arrange
        Faker faker = new Faker();
        ArrayList<AbstractSuite> suiteList = new ArrayList<>();
        AbstractSuite mockedSuite = mock(WebsitesSuite.class);
        suiteList.add(mockedSuite);
        TestAsyncTask task = new TestAsyncTask(a, suiteList);
        Result testResult = ResultFactory.build(new WebsitesSuite(), true, true);

        WebConnectivity test = new WebConnectivity();
        test.setInputs(null);

        when(mockedSuite.getTestList(any())).thenReturn(new WebConnectivity[]{test});
        when(mockedSuite.getResult()).thenReturn(testResult);

        OONICheckInResults listResult = mock(OONICheckInResults.class);
        OONICheckInResults.OONICheckInInfoWebConnectivity ooniCheckInInfoWebConnectivity = mock(OONICheckInResults.OONICheckInInfoWebConnectivity.class);

        String url1 = faker.internet.url();
        OONIURLInfo firstUrl = mock(OONIURLInfo.class);
        when(firstUrl.getUrl()).thenReturn(url1);
        when(firstUrl.getCategoryCode()).thenReturn("");
        when(firstUrl.getCountryCode()).thenReturn(faker.address.countryCode());

        String url2 = faker.internet.url();
        OONIURLInfo secondUrl = mock(OONIURLInfo.class);
        when(secondUrl.getUrl()).thenReturn(url2);
        when(secondUrl.getCategoryCode()).thenReturn("");
        when(secondUrl.getCountryCode()).thenReturn(faker.address.countryCode());

        String url3 = faker.internet.url();
        OONIURLInfo thirdUrl = mock(OONIURLInfo.class);
        when(thirdUrl.getUrl()).thenReturn(url3);
        when(thirdUrl.getCategoryCode()).thenReturn("");
        when(thirdUrl.getCountryCode()).thenReturn(faker.address.countryCode());

        when(ooniCheckInInfoWebConnectivity.getUrls())
                .thenReturn(new ArrayList<>(Arrays.asList(firstUrl, secondUrl, thirdUrl)));

        when(listResult.getWebConnectivity())
                .thenReturn(ooniCheckInInfoWebConnectivity);
        when(ooniSessionMock.checkIn(any(), any())).thenReturn(listResult);

        // Act
        mockedEngine.isTaskDone = true;

        task.execute();
        idleTaskUntilFinished(task);

        // Assert
        assertNotNull(test.getInputs());
        assertArrayEquals(new Object[]{url1, url2, url3}, test.getInputs().toArray());
    }

    @Test
    @Ignore("RunTestService#onCreate call to BroadCast Receiver not triggered")
    // TODO (aanorbel) look for a way to test scenario.
    public void runTest_withProgress() {
        // Arrange
        ArrayList<AbstractSuite> suiteList = new ArrayList<>();
        AbstractSuite mockedSuite = mock(ExperimentalSuite.class);
        suiteList.add(mockedSuite);
        AbstractTest test = mock(AbstractTest.class);
        when(mockedSuite.getTestList(any())).thenReturn(new AbstractTest[]{test});

        TestAsyncTask task = new TestAsyncTask(a, suiteList, false);

        // Act
        task.execute();
        idleTaskUntilFinished(task);
        task.onStart("test");
        task.onProgress(50);
        idleTaskUntilFinished(task);

        // Assert
        verify(builder).setContentText("test");
        verify(builder, atLeast(2)).setProgress(anyInt(), anyInt(), anyBoolean());
        verify(notificationManager, atLeast(2)).notify(anyInt(), any());
    }


    @Test
    @Ignore("RunTestService#onCreate call to BroadCast Receiver not triggered")
    // TODO (aanorbel) look for a way to test scenario.
    public void runTest_withError() {
        // Arrange
        ArrayList<AbstractSuite> suiteList = new ArrayList<>();
        AbstractSuite mockedSuite = mock(ExperimentalSuite.class);
        suiteList.add(mockedSuite);
        AbstractTest test = mock(AbstractTest.class);
        when(mockedSuite.getTestList(any())).thenReturn(new AbstractTest[]{test});
        doThrow(new RuntimeException("")).when(test).run(any(), any(), any(), any(), any(), anyInt(), any());

        TestAsyncTask task = new TestAsyncTask(a, suiteList,false);

        // Act
        task.execute();
        idleTaskUntilFinished(task);

        // Assert
         verify(runService).stopSelf();
    }

    @Test
    public void runTest_interrupt() {
        // Arrange
        ArrayList<AbstractSuite> suiteList = new ArrayList<>();
        AbstractSuite mockedSuite = mock(ExperimentalSuite.class);
        suiteList.add(mockedSuite);
        AbstractTest test = mock(AbstractTest.class);
        when(mockedSuite.getTestList(any())).thenReturn(new AbstractTest[]{test});
        when(test.canInterrupt()).thenReturn(true);

        TestAsyncTask task = new TestAsyncTask(a, suiteList, false);

        // Act
        task.execute();
        idleTaskUntilFinished(task);
        task.interrupt();
        idleTaskUntilFinished(task);

        // Assert
        verify(test).interrupt();
        assertTrue(task.isInterrupted());
    }
}
