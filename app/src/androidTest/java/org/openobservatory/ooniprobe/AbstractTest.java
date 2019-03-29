package org.openobservatory.ooniprobe;

import android.content.Context;

import org.junit.Before;

import androidx.test.platform.app.InstrumentationRegistry;

public class AbstractTest {
	protected Context c;

	@Before public void before() {
		c = InstrumentationRegistry.getInstrumentation().getTargetContext();
	}
}
