package org.openobservatory.ooniprobe.ui.resultdetails;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.openobservatory.ooniprobe.ui.utils.RecyclerViewMatcher.withRecyclerView;
import static org.openobservatory.ooniprobe.ui.utils.ViewMatchers.waitPartialText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.flaky.AllowFlaky;
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class WebsitesTest extends MeasurementAbstractTest {

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

    @Test
    @AllowFlaky(attempts = 3)
    public void testHeaderData() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c), 10, 2);

        // Act
        launchDetails(testResult.id);

        // Assert
        assertMeasurementHeader(testResult);
    }

    @Test
    public void checkListOfMeasurementsTest() {
        // Arrange
        int successfulMeasurements = 10;
        int failedMeasurement = 2;

        Result testResult = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c), successfulMeasurements, failedMeasurement);
        List<Measurement> measurements = testResult.getMeasurementsSorted();

        // Act
        launchDetails(testResult.id);

        // Assert
        for (int i = 0; i < measurements.size(); i++) {
            onData(anything())
                .inAdapterView(withId(R.id.recyclerView))
                .atPosition(i)
                .onChildView(withId(R.id.text))
                .check(matches(withText(containsString(measurements.get(i).getUrlString()))));
        }

    }

    @Test
    public void testSucceed() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c));
        Measurement measurement = testResult.getMeasurementsSorted().get(0);
        String headerOutcome = measurement.getUrlString() + "\n" + getResourceString(R.string.TestResults_Details_Websites_Reachable_Hero_Title);

        // Act
        launchDetails(testResult.id);
        onData(anything()).inAdapterView(withId(R.id.recyclerView)).atPosition(0).perform(click());

        // Assert
        onView(withText(headerOutcome)).check(matches(isDisplayed()));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }

    @Test
    public void testBlocked() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c), 0, 3);
        Measurement measurement = testResult.getMeasurementsSorted().get(0);
        String headerOutcome = measurement.getUrlString() + "\n" + BLOCKED_OUTCOME;

        // Act
        launchDetails(testResult.id);
        onData(anything()).inAdapterView(withId(R.id.recyclerView)).atPosition(0).perform(click());

        // Assert
        onView(withText(headerOutcome)).check(matches(isDisplayed()));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }

    @Test
    @Ignore("We need to mock engine")
    public void canRerunTest() {
        // Arrange
        int successfulMeasurements = 10;
        int failedMeasurement = 2;
        int totalNumberOfMeasurements = successfulMeasurements + failedMeasurement;

        Result testResult = ResultFactory.createAndSave(OONITests.WEBSITES.toOONIDescriptor(c), successfulMeasurements, failedMeasurement);
        String websites = totalNumberOfMeasurements + " " + getResourceString(R.string.websites);
        List<Measurement> measurements = testResult.getMeasurementsSorted();

        // Act
        launchDetails(testResult.id);
        onView(withId(R.id.reRun)).perform(click());
        onView(isRoot()).perform(waitPartialText(websites, TimeUnit.SECONDS.toMillis(3)));
        onView(withText(getResourceString(R.string.Modal_ReRun_Websites_Run))).perform(click());

        // TODO: we can't determine the time, we need to mock or it can be a long wait

        // Assert
        for (int i = 0; i < measurements.size(); i++) {
            onView(withId(R.id.recyclerView))
                    .perform(scrollToPosition(i));

            onView(withRecyclerView(R.id.recyclerView)
                    .atPositionOnView(i, R.id.text))
                    .check(matches(withText(containsString(measurements.get(i).getUrlString()))));
        }

    }

}
