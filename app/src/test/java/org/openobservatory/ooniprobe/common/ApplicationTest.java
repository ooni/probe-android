package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.di.TestAppComponent;
import org.openobservatory.ooniprobe.factory.TestApplication;

import static org.junit.Assert.assertTrue;

@SmallTest
public class ApplicationTest extends RobolectricAbstractTest {
    @Test
    public void packageName() {
        assertTrue(a.getPackageName().startsWith("org.openobservatory.ooniprobe"));
    }

    @Test
    public void testApp() {
        assertTrue(a instanceof TestApplication);
    }

    @Test
    public void component() {
        assertTrue(a.component instanceof TestAppComponent);
    }
}
