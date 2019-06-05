package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;

@SmallTest
public class ResubmitTaskTest {
    @Test
    public void standardTimeout() {
        Assert.assertEquals(ResubmitTask.getTimeout(2000), 11);
    }

    public void zeroTimeout() {
        Assert.assertEquals(ResubmitTask.getTimeout(0), 10);
    }

    public void maxTimeout() {
        Assert.assertEquals(ResubmitTask.getTimeout(Long.MAX_VALUE), Long.MAX_VALUE / 2000 + 10);
    }
}
