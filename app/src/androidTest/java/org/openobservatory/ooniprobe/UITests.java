package org.openobservatory.ooniprobe;

import androidx.test.espresso.contrib.RecyclerViewActions;
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

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(JUnit4.class)
public class UITests {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

    @Test
    public void testCustomURL() {
        onView(withId(R.id.dashboard)).perform(click());
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.customUrl)).perform(click());

        onView(withId(R.id.editText)).perform(typeText("ooni.io"));
        closeSoftKeyboard();
        onView(withId(R.id.runButton)).perform(click());

        onView(withId(R.id.testResults)).perform(click());
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        //TODO TestResults_Details_Websites_Reachable_Content_Paragraph
        onView(withId(R.id.desc)).check(matches(withText(containsString("http://ooni.io is accessible"))));

    }


    //@Test
    public void testSettings() {
        onView(withId(R.id.settings)).perform(click());
        //onView(withId(R.id.privacy)).perform(click());
        //onView(withText(activityRule.getActivity().getResources().getString(R.string.privacy))).perform(click());
    }

}
