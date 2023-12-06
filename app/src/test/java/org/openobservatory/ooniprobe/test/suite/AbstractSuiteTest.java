package org.openobservatory.ooniprobe.test.suite;

import org.junit.Before;
import org.junit.Test;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openobservatory.ooniprobe.test.suite.AbstractSuiteExtensionsKt.getSuite;

public class AbstractSuiteTest {
    private final Application app = mock(Application.class);
    private AbstractSuite suite = new AbstractSuite(
            "test",
            "",
            "",
            R.drawable.test_middle_boxes,
            R.drawable.test_middle_boxes_24,
            R.color.color_violet8,
            R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
            R.style.Theme_MaterialComponents_NoActionBar_App_MiddleBoxes,
            "",
            "anim/middle_boxes.json",
            R.string.small_datausage
    ) {
    };

    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Before
    public void setUp() {
        when(app.getPreferenceManager()).thenReturn(pm);
    }

    @Test
    public void fields() {
        assertEquals("test", suite.getName());
        assertEquals(R.drawable.test_middle_boxes, suite.getIcon());
        assertEquals(R.color.color_violet8, suite.getColor());
        assertEquals(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_MiddleBoxes, suite.getThemeLight());
        assertEquals(R.style.Theme_MaterialComponents_NoActionBar_App_MiddleBoxes, suite.getThemeDark());
        assertEquals("anim/middle_boxes.json", suite.getAnim());
        assertEquals(R.string.small_datausage, suite.getDataUsage());
    }

    @Test
    public void testList() {
        assertNull(suite.getTestList(pm));
        AbstractTest test = new WebConnectivity();
        suite.setTestList(test);
        assertArrayEquals(new AbstractTest[]{test}, suite.getTestList(pm));
    }

    @Test
    public void result() {
        assertEquals("test", suite.getResult().test_group_name);

        Result result = new Result();
        suite.setResult(result);

        assertEquals(result, suite.getResult());
    }

    @Test
    public void getRuntime() {
        AbstractTest test = mock(AbstractTest.class);
        when(test.getRuntime(any())).thenReturn(10);
        suite.setTestList(test);
        assertEquals(10, suite.getRuntime(pm).intValue());

        when(test.getRuntime(any())).thenReturn(-10);
        assertEquals(3600, suite.getRuntime(pm).intValue());
    }

    @Test
    public void asArray() {
        assertEquals(new ArrayList<>(Collections.singletonList(suite)), suite.asArray());
    }

    @Test
    public void getSuiteTest() {
        when(pm.isRunDash()).thenReturn(true);

        AbstractSuite s = getSuite(app, "dash", null, "");

        assertNotNull(s);
        assertEquals(OONITests.PERFORMANCE.getLabel(), s.getName());
        assertEquals(1, s.getTestList(pm).length);
        assertEquals(Dash.class, s.getTestList(pm)[0].getClass());
    }
}
