package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;

import static org.junit.Assert.assertTrue;

@SmallTest
public class ApplicationTest extends RobolectricAbstractTest {
    @Test
    public void packageName() {
        assertTrue(a.getPackageName().startsWith("org.openobservatory.ooniprobe"));
    }
}
