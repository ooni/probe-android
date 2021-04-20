package org.openobservatory.ooniprobe.ui;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;

import java.util.Random;

import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class UITests {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCustomURL() {
        onView(ViewMatchers.withId(R.id.dashboard)).perform(click());
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

        onView(withId(R.id.desc)).check(matches(withText(containsString("http://ooni.io is accessible"))));
    }

    // TODO: Requires specific state to pass
    @Test
    public void testSettings() {
        onView(withId(R.id.settings)).perform(click());
        /*
        References
        https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
        https://stackoverflow.com/questions/38023269/how-to-scroll-a-preferencescreen/38147510
         */
        onView(withId(androidx.preference.R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.Settings_Privacy_Label)),
                        click()));

        //TODO do this only if the switch is enabled
        //complications https://stackoverflow.com/questions/51678563/how-to-access-recyclerview-viewholder-with-espresso
        onView(withId(androidx.preference.R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.Settings_Sharing_UploadResults)),
                        click()));
        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.dashboard)).perform(click());
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.run)).perform(click());

        onView(withId(R.id.testResults)).perform(click());
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Random random = new Random();
        int randomNumber = random.nextInt(3);
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(randomNumber, click()));

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.Snackbar_ResultsNotUploaded_Text)));
    }

}
