package org.openobservatory.ooniprobe.ui;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.RunningActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class OoniRunActivityTest extends AbstractTest {

    private ActivityScenario<RunningActivity> scenario;

    @Before
    public void setUp() {
        a.getPreferenceManager().setShowOnboarding(false);
        a.getPreferenceManager().setAppOpenCount(0L);
    }

    @Test
    public void openValid() {
        String version = BuildConfig.VERSION_NAME.split("-")[0];
        scenario = launch("ooni://nettest?mv=" + version + "&tn=dash");
        onView(withText(R.string.Test_Dash_Fullname)).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void openValidWithUrs() {
        String version = BuildConfig.VERSION_NAME.split("-")[0];
        scenario = launch(
                "ooni://nettest?mv=" + version + "&tn=web_connectivity&ta={\"urls\":[\"http://example.org\"]}"
        );
        onView(withText(R.string.Test_WebConnectivity_Fullname)).check(matches(isDisplayed()));
        onView(withText("http://example.org")).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void openOutdatedVersion() {
        scenario = launch("ooni://nettest?mv=2100.01.01&tn=dash");
        onView(withText(R.string.OONIRun_OONIProbeOutOfDate)).check(matches(isDisplayed()));
        scenario.close();
    }

    private ActivityScenario<RunningActivity> launch(String uri) {
        return ActivityScenario.launch(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }
}
