package org.openobservatory.ooniprobe.test.suite;

import org.junit.Test;
import org.openobservatory.ooniprobe.TestApplicationProvider;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExperimentalSuiteTest {

    private final ExperimentalSuite suite = new ExperimentalSuite(TestApplicationProvider.app().getResources());
    private final ExperimentalSuite autoRunSuite = ExperimentalSuite.initForAutoRun(TestApplicationProvider.app().getResources());
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    public void getTestList_empty() {

        when(pm.isExperimentalOn()).thenReturn(false);

        AbstractTest[] tests = suite.getTestList(pm);

        assertEquals(0, tests.length);
    }

    @Test
    public void getTestList_experimental_on() {
        when(pm.isExperimentalOn()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(2, tests.size());
        assertEquals(Experimental.class, tests.get(0).getClass());
        assertEquals(Experimental.class, tests.get(1).getClass());
        assertEquals("stunreachability", tests.get(0).getName());
        assertEquals("dnscheck", tests.get(1).getName());
    }

    @Test
    public void getTestList_experimental_on_autorun_on() {
        when(pm.isExperimentalOn()).thenReturn(true);
        autoRunSuite.setAutoRun(true);

        List<AbstractTest> tests = Arrays.asList(autoRunSuite.getTestList(pm));

        assertEquals(4, tests.size());
        assertEquals(Experimental.class, tests.get(0).getClass());
        assertEquals(Experimental.class, tests.get(1).getClass());
        assertEquals(Experimental.class, tests.get(2).getClass());
        assertEquals(Experimental.class, tests.get(3).getClass());

        assertEquals("torsf", tests.get(0).getName());
        assertEquals("vanilla_tor", tests.get(1).getName());
        assertEquals("stunreachability", tests.get(2).getName());
        assertEquals("dnscheck", tests.get(3).getName());
    }
}
