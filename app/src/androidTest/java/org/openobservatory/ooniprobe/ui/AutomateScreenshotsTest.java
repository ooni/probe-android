package org.openobservatory.ooniprobe.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class AutomateScreenshotsTest extends AbstractTest {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    public ActivityScenario<MainActivity> scenario;

    @Before
    @Override
    public void before() {
        super.before();
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        a.getPreferenceManager().setShowOnboarding(false);
        a.getPreferenceManager().setAppOpenCount(0L);
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    // TODO: Avoid the needing a clean state and to run after the WebConnectivityTest
    @Test
    public void testTakeScreenshot() {
        //Dashboard
        Screengrab.screenshot("01_dashboard");

        //Test results
        onView(ViewMatchers.withId(R.id.testResults)).perform(click());
        Screengrab.screenshot("02_testResults");

        //Blocked Website details
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(4, click()));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        Screengrab.screenshot("03_blockedwebsiste");

        Espresso.pressBack();
        Espresso.pressBack();

        //Dash details
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Screengrab.screenshot("04_dash");

        //Custom url
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.dashboard)).perform(click());
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.customUrl)).perform(click());
        Screengrab.screenshot("05_customurl");
    }
}

