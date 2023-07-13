package org.openobservatory.ooniprobe.test.suite;

import org.junit.Test;
import org.openobservatory.ooniprobe.TestApplicationProvider;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Tor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CircumventionSuiteTest {

    private final CircumventionSuite suite = new CircumventionSuite(TestApplicationProvider.app().getResources());
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    public void getTestList_empty() {
        when(pm.isTestPsiphon()).thenReturn(false);
        when(pm.isTestTor()).thenReturn(false);
        when(pm.isTestRiseupVPN()).thenReturn(false);

        AbstractTest[] tests = suite.getTestList(pm);

        assertEquals(0, tests.length);
    }

    @Test
    public void getTestList_full() {
        when(pm.isTestPsiphon()).thenReturn(true);
        when(pm.isTestTor()).thenReturn(true);
        when(pm.isTestRiseupVPN()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        // Psiphon and Tor. Riseup VPN has been temporarily disabled.
        assertEquals(2, tests.size());
        assertTrue(findTestClass(tests, Psiphon.class));
        assertTrue(findTestClass(tests, Tor.class));
//        assertTrue(findTestClass(tests, RiseupVPN.class));
    }

    private boolean findTestClass(List<AbstractTest> tests, Class<? extends AbstractTest> klass) {
        return tests.stream().map(AbstractTest::getClass).anyMatch(c -> c == klass);
    }
}
