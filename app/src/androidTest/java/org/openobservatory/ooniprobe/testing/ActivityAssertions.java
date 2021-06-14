package org.openobservatory.ooniprobe.testing;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import java.util.Collection;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.openobservatory.ooniprobe.testing.WaitFor.waitFor;

public class ActivityAssertions {

    @Nullable
    public static Activity getCurrentActivity() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> activities =
                    ActivityLifecycleMonitorRegistry.getInstance()
                            .getActivitiesInStage(Stage.RESUMED);
            if (activities.iterator().hasNext()) {
                activity[0] = activities.iterator().next();
            }
        });
        return activity[0];
    }

    public static void assertCurrentActivity(Class<? extends Activity> activityClass) {
        String currentActivityName = "";
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivityName = currentActivity.getClass().getName();
        }
        assertEquals(activityClass.getName(), currentActivityName);
    }

    public static void waitForCurrentActivity(Class<? extends Activity> activityClass) {
        waitFor(() -> assertCurrentActivity(activityClass));
    }
}
