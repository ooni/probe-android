package org.openobservatory.ooniprobe;

import android.content.Context;

import org.junit.Before;
import org.openobservatory.ooniprobe.common.Application;

import androidx.test.platform.app.InstrumentationRegistry;

public class AbstractTest {
	protected Context c;
	protected Application a;

	@Before public void before() {
		c = InstrumentationRegistry.getInstrumentation().getTargetContext();
		a = (Application) c.getApplicationContext();
	}
}
