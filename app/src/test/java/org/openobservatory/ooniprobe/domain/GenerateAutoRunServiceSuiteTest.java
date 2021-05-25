package org.openobservatory.ooniprobe.domain;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONICheckInResults.OONICheckInInfoWebConnectivity;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.test.EngineInterface;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.ArrayList;

import io.bloco.faker.Faker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class GenerateAutoRunServiceSuiteTest extends RobolectricAbstractTest {

    // Mocks
    OONICheckInInfoWebConnectivity webConnectivityMock = mock(OONICheckInInfoWebConnectivity.class);
    PreferenceManager preferenceManagerMock = mock(PreferenceManager.class);
    OONICheckInConfig ooniCheckConfigMock = mock(OONICheckInConfig.class);
    OONICheckInResults ooniResultsMock = mock(OONICheckInResults.class);
    OONISession ooniSessionMock = mock(OONISession.class);

    // Engine && UseCase
    EngineInterface mockedEngine = new TestEngineInterface(ooniSessionMock);
    GenerateAutoRunServiceSuite generateSuite;

    @Override
    public void setUp() {
        super.setUp();
        generateSuite = new GenerateAutoRunServiceSuite(a, preferenceManagerMock);
        EngineProvider.engineInterface = mockedEngine;

        when(preferenceManagerMock.testWifiOnly()).thenReturn(true);
        when(preferenceManagerMock.testChargingOnly()).thenReturn(true);
    }

    @Test
    public void shouldNotStartTest() {
        // Act
        AbstractSuite suite = generateSuite.generate(ooniCheckConfigMock, false, false);

        // Assert
        Assert.assertNull(suite);
    }

    @Test
    public void generateSuite() throws Exception {
        // Arrange
        ArrayList<OONIURLInfo> suiteUrls = getTestUrls();

        when(preferenceManagerMock.getEnabledCategoryArr()).thenReturn(new ArrayList<String>() {
            {
                add("ALDR");
                add("REL");
                add("PORN");
                add("PROV");
                add("POLR");
                add("HUMR");
                add("ENV");
            }
        });

        when(webConnectivityMock.getUrls()).thenReturn(suiteUrls);
        when(ooniResultsMock.getWebConnectivity()).thenReturn(webConnectivityMock);
        when(ooniSessionMock.checkIn(any(), any())).thenReturn(ooniResultsMock);

        // Act
        AbstractSuite suite = generateSuite.generate(ooniCheckConfigMock, true, true);

        // Assert
        Assert.assertNotNull(suite);
        verify(preferenceManagerMock, times(1)).updateAutorunDate();
        verify(preferenceManagerMock, times(1)).incrementAutorun();
        Assert.assertEquals(1, suite.getTestList(preferenceManagerMock).length);

        AbstractTest webTest = suite.getTestList(preferenceManagerMock)[0];

        Assert.assertEquals("web_connectivity", webTest.getName());
        Assert.assertEquals(suiteUrls.size(), webTest.getInputs().size());

        for (int i = 0; i < webTest.getInputs().size(); i++) {
            Assert.assertEquals( webTest.getInputs().get(i), suiteUrls.get(i).getUrl());
        }
    }

    private ArrayList<OONIURLInfo> getTestUrls() {
        Faker faker = new Faker();

        OONIURLInfo url1 = mock(OONIURLInfo.class);
        when(url1.getUrl()).thenReturn(faker.internet.url());

        OONIURLInfo url2 = mock(OONIURLInfo.class);
        when(url1.getUrl()).thenReturn(faker.internet.url());

        OONIURLInfo url3 = mock(OONIURLInfo.class);
        when(url1.getUrl()).thenReturn(faker.internet.url());

        return new ArrayList<OONIURLInfo>(){
            {
                add(url1);
                add(url2);
                add(url3);
            }
        };
    }

}