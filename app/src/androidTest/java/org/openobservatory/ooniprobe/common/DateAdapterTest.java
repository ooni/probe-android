package org.openobservatory.ooniprobe.common;

import org.junit.Assert;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;

import java.util.Date;

import androidx.test.filters.SmallTest;

@SmallTest public class DateAdapterTest extends AbstractTest {
	private static final String STRING = "\"1970-01-01 00:00:00\"";
	private static final Date DATE = new Date(0);

	@Test public void write() {
		Assert.assertEquals(STRING, a.getGson().toJson(DATE));
	}

	@Test public void read() {
		Assert.assertEquals(DATE, a.getGson().fromJson(STRING, Date.class));
	}
}
