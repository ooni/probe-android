package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@SmallTest
public class ApplicationTest extends RobolectricAbstractTest {
    @Test
    public void packageName() {
        assertEquals("org.openobservatory.ooniprobe.dev", a.getPackageName());
    }
}
