package org.openobservatory.ooniprobe;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.openobservatory.ooniprobe.common.Application;

import static org.junit.Assume.assumeTrue;

public class AbstractTest {
    protected Context c;
    protected Application a;

    @Before
    public void before() {
        c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        a = (Application) c.getApplicationContext();

        a.getPreferenceManager().setShowOnboarding(false);
        a.getPreferenceManager().setAppOpenCount(0L);
    }

    protected String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return targetContext.getResources().getString(id);
    }

    protected void onlyRunForAutomationFlag() {
        // This assumption will be false, causing the test to halt and be ignored.
        assumeTrue("Test ignored because RUN_AUTOMATION=false", BuildConfig.RUN_AUTOMATION);
    }
}
