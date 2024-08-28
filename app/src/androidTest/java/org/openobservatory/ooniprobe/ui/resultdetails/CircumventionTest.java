package org.openobservatory.ooniprobe.ui.resultdetails;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.adevinta.android.barista.rule.flaky.AllowFlaky;
import com.adevinta.android.barista.rule.flaky.FlakyTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.utils.FormattingUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CircumventionTest extends MeasurementAbstractTest {

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

    @Test
    @AllowFlaky(attempts = 3)
    public void testHeaderData() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new CircumventionSuite(), 3, 0);

        // Act
        launchDetails(testResult.id);

        // Assert
        assertMeasurementHeader(testResult);
    }

    @Test
    public void testSuccessPsiphon() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new CircumventionSuite(), 3, 0);
        Measurement measurement = testResult.getMeasurement("psiphon");
        String formattedBootstrap = FormattingUtils.formatBootstrap(measurement.getTestKeys().bootstrap_time);

        // Act
        launchDetails(testResult.id);
        onView(withText("Psiphon Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(true);
        onView(withId(R.id.bootstrap)).check(matches(withText(formattedBootstrap)));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }

    @Test
    public void testBlockedPsiphon() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new CircumventionSuite(), 0, 3);
        Measurement measurement = testResult.getMeasurement("psiphon");

        // Act
        launchDetails(testResult.id);
        onView(withText("Psiphon Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        onView(withId(R.id.bootstrap)).check(matches(withText(TEST_RESULTS_NOT_AVAILABLE)));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }

    @Test
    public void testSuccessTor() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new CircumventionSuite(), 3, 0);
        Measurement measurement = testResult.getMeasurement("tor");

        String formattedBridges = FormattingUtils.getFormattedBridges(measurement);
        String formattedAuthorities = FormattingUtils.getFormattedAuthorities(measurement);

        // Act
        launchDetails(testResult.id);
        onView(withText("Tor Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(true);
        onView(withId(R.id.bridges)).check(matches(withText(formattedBridges)));
        onView(withId(R.id.authorities)).check(matches(withText(formattedAuthorities)));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }

    @Test
    public void testBlockedTor() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(new CircumventionSuite(), 0, 3);
        Measurement measurement = testResult.getMeasurement("tor");

        String formattedBridges = FormattingUtils.getFormattedBridges(measurement);
        String formattedAuthorities = FormattingUtils.getFormattedAuthorities(measurement);

        // Act
        launchDetails(testResult.id);
        onView(withText("Tor Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        onView(withId(R.id.bridges)).check(matches(withText(formattedBridges)));
        onView(withId(R.id.authorities)).check(matches(withText(formattedAuthorities)));
        assertMeasurementRuntimeAndNetwork(measurement, testResult.network);
    }
}
