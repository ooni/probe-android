package org.openobservatory.ooniprobe.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.flaky.AllowFlaky;
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.fragment.ProgressFragment;

import io.bloco.faker.Faker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.openobservatory.ooniprobe.ui.utils.RecyclerViewMatcher.withRecyclerView;
import static org.openobservatory.ooniprobe.ui.utils.ViewMatchers.withIndex;

@RunWith(AndroidJUnit4.class)
public class MainActivityWebsitesTest extends AbstractTest {

    public ActivityScenario<MainActivity> scenario;

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

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
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.title))
                .perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        onView(withIndex(withId(R.id.editText), 0))
                .perform(typeText(url1));

        onView(withId(R.id.add)).perform(click());

        onView(withIndex(withId(R.id.editText), 1))
                .perform(typeText(url2));

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
        String totalUrls = String.format(getResourceString(R.string.OONIRun_URLs), 1);

        // Act
        launchDashboard();
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.title))
                .perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        onView(withIndex(withId(R.id.editText), 0))
                .perform(typeText(url1));

        onView(withIndex(withId(R.id.delete), 0))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.add)).perform(click());

        onView(withIndex(withId(R.id.editText), 1))
                .perform(typeText(url2));

        onView(withIndex(withId(R.id.delete), 1))
                .perform(click());

        // Assert
        onView(withText(totalUrls)).check(matches(isDisplayed()));
    }

    @Test
    @AllowFlaky(attempts = 3)
    public void lunchCustomWebsiteIntentTest() {
        // Act
        launchDashboard();
        onView(withId(R.id.recycler))
                .perform(scrollToPosition(0));

        onView(withRecyclerView(R.id.recycler)
                .atPositionOnView(0, R.id.title))
                .perform(click());

        onView(withId(R.id.customUrl)).perform(click());

        Intents.init();

        Intent emptyIntent = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, emptyIntent);
        intending(anyIntent()).respondWith(result);


        onView(withId(R.id.runButton)).perform(click());
        // TODO(aanorbel): resolve issue. Running activity no longer available.
        //   ```intended(hasComponent(hasClassName(RunningActivity.class.getName())));```
        //   progress display is dismissed before its availability is checked
        //   ```onView(withId(R.id.progress_layout)).check(matches(isDisplayed()));```

        Intents.release();
    }

    public void launchDashboard() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        scenario = ActivityScenario.launch(intent);
    }
}
