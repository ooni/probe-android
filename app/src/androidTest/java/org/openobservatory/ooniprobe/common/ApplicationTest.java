package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;

@SmallTest public class ApplicationTest extends AbstractTest {
	@Test public void packageName() {
		Assert.assertEquals("org.openobservatory.ooniprobe", c.getPackageName());
	}
}
