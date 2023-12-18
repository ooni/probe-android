package org.openobservatory.ooniprobe.test.suite;

import org.junit.Ignore;
import org.junit.Test;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PerformanceSuiteTest {
    private final Application app = mock(Application.class);

    private final AbstractSuite suite = OONITests.PERFORMANCE.toOONIDescriptor(app).getTest(app);
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    @Ignore("Preference check if not used since the tests to run are determined by the #RunTestsActivity")
    public void getTestList_empty() {
        when(pm.isRunNdt()).thenReturn(false);
        when(pm.isRunDash()).thenReturn(false);
        when(pm.isRunHttpHeaderFieldManipulation()).thenReturn(false);
        when(pm.isRunHttpInvalidRequestLine()).thenReturn(false);

        AbstractTest[] tests = suite.getTestList(pm);

        assertEquals(0, tests.length);
    }

    @Test
    @Ignore("Preference check if not used since the tests to run are determined by the #RunTestsActivity")
    public void getTestList_full() {
        when(pm.isRunNdt()).thenReturn(true);
        when(pm.isRunDash()).thenReturn(true);
        when(pm.isRunHttpHeaderFieldManipulation()).thenReturn(true);
        when(pm.isRunHttpInvalidRequestLine()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(4, tests.size());
        assertTrue(findTestClass(tests, Ndt.class));
        assertTrue(findTestClass(tests, Dash.class));
        assertTrue(findTestClass(tests, HttpHeaderFieldManipulation.class));
        assertTrue(findTestClass(tests, HttpInvalidRequestLine.class));
    }

    private boolean findTestClass(List<AbstractTest> tests, Class<? extends AbstractTest> klass) {
        return tests.stream().map(AbstractTest::getClass).anyMatch(c -> c == klass);
    }
}
