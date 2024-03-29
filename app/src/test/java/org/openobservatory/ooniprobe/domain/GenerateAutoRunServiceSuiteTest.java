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
    PreferenceManager preferenceManagerMock = mock(PreferenceManager.class);
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
    public void generateSuite() {
        // Act
        AbstractSuite suite = generateSuite.generate();

        // Assert
        Assert.assertNotNull(suite);
        Assert.assertEquals(1, suite.getTestList(preferenceManagerMock).length);

        AbstractTest webTest = suite.getTestList(preferenceManagerMock)[0];

        Assert.assertEquals("web_connectivity", webTest.getName());
        Assert.assertNull(webTest.getInputs());

    }

}