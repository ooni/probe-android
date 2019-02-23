package org.openobservatory.ooniprobe;

import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.common.ApplicationTest;
import org.openobservatory.ooniprobe.model.database.NetworkTest;

/**
 * Run this class to execute all Android Instrumented Test
 */
@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
		ApplicationTest.class,
		NetworkTest.class
})
public class Suite {
}
