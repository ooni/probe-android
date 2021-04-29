package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;

import static org.junit.Assert.assertEquals;

@SmallTest
public class ResubmitTaskTest {
    @Test
    public void standardTimeout() {
        assertEquals(ResubmitTask.getTimeout(2000), 11);
    }

    @Test
    public void zeroTimeout() {
        assertEquals(ResubmitTask.getTimeout(0), 10);
    }

    @Test
    public void maxTimeout() {
        assertEquals(ResubmitTask.getTimeout(Long.MAX_VALUE), Long.MAX_VALUE / 2000 + 10);
    }
}
