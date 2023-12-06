package org.openobservatory.ooniprobe.ui.resultdetails;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.flaky.AllowFlaky;
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.factory.ResultFactory;
import org.openobservatory.ooniprobe.model.database.Result;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class InstantMessagingTest extends MeasurementAbstractTest {

    @Rule
    public FlakyTestRule flakyRule = new FlakyTestRule();

    @Test
    @AllowFlaky(attempts = 3)
    public void testHeaderData() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));

        // Act
        launchDetails(testResult.id);

        // Assert
        assertMeasurementHeader(testResult);
    }

    @Test
    @AllowFlaky(attempts = 2)
    public void testSuccessWhatsApp() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));

        // Act
        launchDetails(testResult.id);
        onView(withText("WhatsApp Test")).check(matches(isDisplayed())).perform(click());
        // Assert
        assertMeasurementOutcome(true);
        assertWhatsAppMeasurement(true);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("whatsapp"), testResult.network);
    }

    @Test
    public void testBlockedWhatsApp() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c), 0, 4);

        // Act
        launchDetails(testResult.id);
        onView(withText("WhatsApp Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        assertWhatsAppMeasurement(false);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("whatsapp"), testResult.network);
    }

    @Test
    public void testSuccessTelegram() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));

        // Act
        launchDetails(testResult.id);
        onView(withText("Telegram Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(true);
        assertTelegramMeasurement(true);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("telegram"), testResult.network);
    }

    @Test
    public void testBlockedTelegram() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c), 0, 4);

        // Act
        launchDetails(testResult.id);
        onView(withText("Telegram Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        assertTelegramMeasurement(false);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("telegram"), testResult.network);
    }

    @Test
    public void testSuccessFacebookMessenger() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));

        // Act
        launchDetails(testResult.id);
        onView(withText("Facebook Messenger Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(true);
        assertFacebookMessengerMeasurement(true);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("facebook_messenger"), testResult.network);
    }

    @Test
    public void testBlockedFacebookMessenger() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c), 0, 4);

        // Act
        launchDetails(testResult.id);
        onView(withText("Facebook Messenger Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        assertFacebookMessengerMeasurement(false);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("facebook_messenger"), testResult.network);
    }

    @Test
    public void testSuccessSignal() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c));

        // Act
        launchDetails(testResult.id);
        onView(withText("Signal Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(true);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("signal"), testResult.network);
    }

    @Test
    public void testBlockedSignal() {
        // Arrange
        Result testResult = ResultFactory.createAndSave(OONITests.INSTANT_MESSAGING.toOONIDescriptor(c), 0, 4);

        // Act
        launchDetails(testResult.id);
        onView(withText("Signal Test")).check(matches(isDisplayed())).perform(click());

        // Assert
        assertMeasurementOutcome(false);
        assertMeasurementRuntimeAndNetwork(testResult.getMeasurement("signal"), testResult.network);
    }

    private void assertWhatsAppMeasurement(boolean wasSuccess) {
        String status = wasSuccess ? SUCCESSFUL_MEASUREMENT : BLOCKED_MEASUREMENT;

        onView(withId(R.id.application)).check(matches(withText(status)));
        onView(withId(R.id.webApp)).check(matches(withText(status)));
        onView(withId(R.id.registrations)).check(matches(withText(status)));
    }

    private void assertTelegramMeasurement(boolean wasSuccess) {
        String status = wasSuccess ? SUCCESSFUL_MEASUREMENT : BLOCKED_MEASUREMENT;

        onView(withId(R.id.application)).check(matches(withText(status)));
        onView(withId(R.id.webApp)).check(matches(withText(status)));
    }

    private void assertFacebookMessengerMeasurement(boolean wasSuccess) {
        String status = wasSuccess ? SUCCESSFUL_MEASUREMENT : BLOCKED_MEASUREMENT;

        onView(withId(R.id.tcp)).check(matches(withText(status)));
        onView(withId(R.id.dns)).check(matches(withText(status)));
    }

}
