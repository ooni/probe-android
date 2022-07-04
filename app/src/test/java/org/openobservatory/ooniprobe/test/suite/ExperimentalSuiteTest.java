package org.openobservatory.ooniprobe.test.suite;

import org.junit.Test;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExperimentalSuiteTest {

    private final ExperimentalSuite suite = new ExperimentalSuite();
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    public void getTestList() {
        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(2, tests.size());
        assertEquals(Experimental.class, tests.get(0).getClass());
        assertEquals(Experimental.class, tests.get(1).getClass());
        assertEquals("stunreachability", tests.get(0).getName());
        assertEquals("dnscheck", tests.get(1).getName());
    }
}
