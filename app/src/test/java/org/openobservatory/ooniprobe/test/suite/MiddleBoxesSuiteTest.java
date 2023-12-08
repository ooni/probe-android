package org.openobservatory.ooniprobe.test.suite;

import org.junit.Ignore;
import org.junit.Test;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MiddleBoxesSuiteTest {

    private final MiddleBoxesSuite suite = new MiddleBoxesSuite();
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    @Ignore("Preference check if not used since the tests to run are determined by the #RunTestsActivity")
    public void getTestList_empty() {
        when(pm.isRunHttpHeaderFieldManipulation()).thenReturn(false);
        when(pm.isRunHttpInvalidRequestLine()).thenReturn(false);

        AbstractTest[] tests = suite.getTestList(pm);

        assertEquals(0, tests.length);
    }

    @Test
    @Ignore("Preference check if not used since the tests to run are determined by the #RunTestsActivity")
    public void getTestList_full() {
        when(pm.isRunHttpHeaderFieldManipulation()).thenReturn(true);
        when(pm.isRunHttpInvalidRequestLine()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(2, tests.size());
        assertTrue(findTestClass(tests, HttpHeaderFieldManipulation.class));
        assertTrue(findTestClass(tests, HttpInvalidRequestLine.class));
    }

    private boolean findTestClass(List<AbstractTest> tests, Class<? extends AbstractTest> klass) {
        return tests.stream().map(AbstractTest::getClass).anyMatch(c -> c == klass);
    }
}
