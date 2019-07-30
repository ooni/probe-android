package org.openobservatory.ooniprobe;

import android.content.Context;

import org.junit.Before;
import org.openobservatory.ooniprobe.common.Application;

import androidx.test.platform.app.InstrumentationRegistry;

public class AbstractTest {
	protected Context context;
	protected Application application;

	@Before public void before() {
		context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		application = (Application) context.getApplicationContext();
	}
}
