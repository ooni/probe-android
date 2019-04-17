package org.openobservatory.ooniprobe;

import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.common.ApplicationTest;
import org.openobservatory.ooniprobe.common.DateAdapterTest;
import org.openobservatory.ooniprobe.model.database.NetworkTest;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeysTest;

/**
 * Run this class to execute all Android Instrumented Test
 */
@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
		ApplicationTest.class,
		DateAdapterTest.class,
		NetworkTest.class,
		TestKeysTest.class
})
public class Suite {
}
