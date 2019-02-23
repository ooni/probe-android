package org.openobservatory.ooniprobe.common;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;

import androidx.test.filters.SmallTest;

@SmallTest public class ApplicationTest extends AbstractTest {
	@Test public void packageName() {
		Assert.assertEquals("org.openobservatory.ooniprobe", c.getPackageName());
	}
}
