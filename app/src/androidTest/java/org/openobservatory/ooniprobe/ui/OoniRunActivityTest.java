package org.openobservatory.ooniprobe.ui;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.RunningActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class OoniRunActivityTest extends AbstractTest {

    String version = BuildConfig.VERSION_NAME.split("-")[0];

    private ActivityScenario<RunningActivity> scenario;

    @Test
    public void openValid() {
        scenario = launch("ooni://nettest?mv=" + version + "&tn=dash");
        onView(withText(R.string.Test_Dash_Fullname)).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void openValidWithUrs() {
        scenario = launch(
            "ooni://nettest?mv=" + version + "&tn=web_connectivity&ta={\"urls\":[\"http://example.org\"]}"
        );
        onView(withText(R.string.Test_WebConnectivity_Fullname)).check(matches(isDisplayed()));
        onView(withText("http://example.org")).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openPartialInputs() {
        scenario = launch("ooni://nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2F%22%5D%7D&mv=" + version);
        onView(withText(R.string.Test_WebConnectivity_Fullname)).check(matches(isDisplayed()));
        onView(withText("http://")).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openValidUrls() {
        scenario = launch("ooni://nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2Fwww.google.it%22%2C%22https%3A%2F%2Frun.ooni.io%2F%22%5D%7D&mv=" + version);
        onView(withText(R.string.Test_WebConnectivity_Fullname)).check(matches(isDisplayed()));
        onView(withText("http://www.google.it")).check(matches(isDisplayed()));
        onView(withText("https://run.ooni.io/")).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openMalformedUrl() {
        scenario = launch("ooni://nettest?tn=web_connectivity&ta=%7B%22urls%22%3A%5B%22http%3A%2F%2Fwww.google.it%22%2C%22https%3A%2F%2Frun.ooni.io&mv=" + version);
        onView(withText(R.string.OONIRun_InvalidParameter)).check(matches(isDisplayed()));
        onView(withText(R.string.OONIRun_InvalidParameter_Msg)).check(matches(isDisplayed()));
        checkRunButtonIsSetToCloseView();
        scenario.close();
    }

    @Test
    public void openNdt() {
        scenario = launch("ooni://nettest?tn=ndt&mv=" + version);
        onView(withText(R.string.Test_NDT_Fullname)).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openDash() {
        scenario = launch("ooni://nettest?tn=dash&mv=" + version);
        onView(withText(R.string.Test_Dash_Fullname)).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openHttpInvalidRequestLine() {
        scenario = launch("ooni://nettest?tn=http_invalid_request_line&mv=" + version);
        onView(withText(R.string.Test_HTTPInvalidRequestLine_Fullname)).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openHttpHeaderFieldManipulation() {
        scenario = launch("ooni://nettest?tn=http_header_field_manipulation&mv=" + version);
        onView(withText(R.string.Test_HTTPHeaderFieldManipulation_Fullname)).check(matches(isDisplayed()));
        checkRunButtonIsDisplayed();
        scenario.close();
    }

    @Test
    public void openInvalidTestName() {
        scenario = launch("ooni://nettest?tn=antani&mv=" + version);
        onView(withText(R.string.OONIRun_InvalidParameter)).check(matches(isDisplayed()));
        checkRunButtonIsSetToCloseView();
        scenario.close();
    }

    @Test
    public void openOutdatedVersion() {
        scenario = launch("ooni://nettest?mv=2100.01.01&tn=dash");
        onView(withText(R.string.OONIRun_OONIProbeOutOfDate)).check(matches(isDisplayed()));
        onView(withId(R.id.run))
            .check(matches(withText(R.string.OONIRun_Update)))
            .check(matches(isDisplayed()));
        scenario.close();
    }

    private static void checkRunButtonIsDisplayed() {
        onView(withId(R.id.run))
            .check(matches(withText(R.string.OONIRun_Run)))
            .check(matches(isDisplayed()));
    }

    private static void checkRunButtonIsSetToCloseView() {
        onView(withId(R.id.run))
            .check(matches(withText(R.string.OONIRun_Close)))
            .check(matches(isDisplayed()));
    }


    private ActivityScenario<RunningActivity> launch(String uri) {
        return ActivityScenario.launch(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }
}
