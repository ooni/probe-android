package org.openobservatory.ooniprobe;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openobservatory.ooniprobe.activity.MainActivity;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(JUnit4.class)
public class AutomateScreenshotsTest {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

    @Test
    public void testTakeScreenshot() {
        //Dashboard
        Screengrab.screenshot("01_dashboard");

        //Screenshot overview
        //onView(withId(R.id.recycler))
         //       .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Test results
        onView(withId(R.id.testResults)).perform(click());
        Screengrab.screenshot("02_testResults");

        //Thepiratebay fetails
        //Dash details
        //Custom url

        // Your custom onView...
        //onView(withId(R.id.run_all)).perform(click());
    }
}

