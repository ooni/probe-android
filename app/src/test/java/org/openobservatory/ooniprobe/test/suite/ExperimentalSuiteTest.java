package org.openobservatory.ooniprobe.test.suite;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.res.Resources;

public class ExperimentalSuiteTest {
    private final Application app = mock(Application.class);
    private final Resources mockContextResources = mock(Resources.class);

    private AbstractSuite suite;
    private AbstractSuite autoRunSuite;
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Before
    public void setUp() {
        when(app.getPreferenceManager()).thenReturn(pm);
        when(app.getResources()).thenReturn(mockContextResources);
        when(mockContextResources.getString(anyInt())).thenReturn("mocked string");
        when(mockContextResources.getString(anyInt(),any())).thenReturn("mocked string");

        suite =  OONITests.EXPERIMENTAL.toOONIDescriptor(app).getTest(app);
    }

    @Test
    public void getTestList_experimental_foreground() {
        when(pm.isExperimentalOn()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(4, tests.size());
        assertEquals(Experimental.class, tests.get(0).getClass());
        assertEquals(Experimental.class, tests.get(1).getClass());
        assertEquals(RiseupVPN.class, tests.get(2).getClass());
        assertEquals(Experimental.class, tests.get(3).getClass());
        assertEquals("stunreachability", tests.get(0).getName());
        assertEquals("dnscheck", tests.get(1).getName());
        assertEquals("riseupvpn", tests.get(2).getName());
        assertEquals("echcheck", tests.get(3).getName());
    }

    @Test
    public void getTestList_experimental_on_autorun_on() {
        when(pm.isExperimentalOn()).thenReturn(true);
        autoRunSuite = OONITests.EXPERIMENTAL.toOONIDescriptor(app).getTest(app);
        autoRunSuite.setAutoRun(true);

        List<AbstractTest> tests = Arrays.asList(autoRunSuite.getTestList(pm));

        assertEquals(6, tests.size());
        assertEquals(Experimental.class, tests.get(0).getClass());
        assertEquals(Experimental.class, tests.get(1).getClass());
        assertEquals(RiseupVPN.class, tests.get(2).getClass());
        assertEquals(Experimental.class, tests.get(3).getClass());
        assertEquals(Experimental.class, tests.get(4).getClass());
        assertEquals(Experimental.class, tests.get(5).getClass());

        assertEquals("stunreachability", tests.get(0).getName());
        assertEquals("dnscheck", tests.get(1).getName());
        assertEquals("riseupvpn", tests.get(2).getName());
        assertEquals("echcheck", tests.get(3).getName());
        assertEquals("torsf", tests.get(4).getName());
        assertEquals("vanilla_tor", tests.get(5).getName());
    }
}
