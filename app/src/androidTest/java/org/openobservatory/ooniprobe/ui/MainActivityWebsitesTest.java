package org.openobservatory.ooniprobe.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.openobservatory.ooniprobe.ui.utils.RecyclerViewMatcher.withRecyclerView;
import static org.openobservatory.ooniprobe.ui.utils.ViewMatchers.withIndex;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.adevinta.android.barista.rule.flaky.AllowFlaky;
import com.adevinta.android.barista.rule.flaky.FlakyTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;

import io.bloco.faker.Faker;

@RunWith(AndroidJUnit4.class)
public class MainActivityWebsitesTest extends AbstractTest {

    public ActivityScenario<MainActivity> scenario;

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void addCustomWebsiteTest() {
        // Arrange
        Faker faker = new Faker();
        String url1 = faker.internet.domainName() + faker.internet.domainSuffix();
        String url2 = faker.internet.domainName() + faker.internet.domainSuffix();
        String totalUrls = String.format(getResourceString(R.string.OONIRun_URLs), 2);


        // Act
        launchDashboard();
        onView(withId(R.id.recycler)).perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler).atPositionOnView(1, R.id.title)).perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        onView(withIndex(withId(R.id.editText), 0)).perform(typeText(url1));

        onView(withId(R.id.add)).perform(click());

        onView(withIndex(withId(R.id.editText), 1)).perform(typeText(url2));

        // Assert
        onView(withText(totalUrls)).check(matches(isDisplayed()));
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void deleteCustomWebsiteTest() {
        // Arrange
        Faker faker = new Faker();
        String url1 = faker.internet.domainName() + faker.internet.domainSuffix();
        String url2 = faker.internet.domainName() + faker.internet.domainSuffix();
        String totalUrls = String.format("Test %s URLs", 1);

        // Act
        launchDashboard();
        onView(withId(R.id.recycler)).perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler).atPositionOnView(1, R.id.title)).perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        onView(withIndex(withId(R.id.editText), 0)).perform(typeText(url1));

        onView(withId(R.id.urlContainer)).perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.urlContainer).atPositionOnView(0, R.id.delete)).check(matches(isDisplayed()));

        onView(withId(R.id.add)).perform(click());

        onView(withIndex(withId(R.id.editText), 1)).perform(typeText(url2));

        onView(withIndex(withId(R.id.delete), 1)).perform(click());

        onView(withId(R.id.urlContainer)).perform(scrollToPosition(1));

        // TODO: fix click action
        onView(withRecyclerView(R.id.urlContainer).atPosition(1)).perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithId(R.id.delete)));

        // Assert
        onView(withText(totalUrls)).check(matches(isDisplayed()));
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void lunchCustomWebsiteIntentTest() {
        // Act
        launchDashboard();
        onView(withId(R.id.recycler)).perform(scrollToPosition(1));

        onView(withRecyclerView(R.id.recycler).atPositionOnView(1, R.id.title)).perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        Intents.init();

        Intent emptyIntent = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, emptyIntent);
        intending(anyIntent()).respondWith(result);

        Intents.release();
    }

    public void launchDashboard() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        scenario = ActivityScenario.launch(intent);
    }
}
