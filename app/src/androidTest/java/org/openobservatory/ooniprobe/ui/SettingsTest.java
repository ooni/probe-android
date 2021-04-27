package org.openobservatory.ooniprobe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openobservatory.ooniprobe.AbstractTest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;

import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SettingsTest extends AbstractTest {

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    public ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        a.getPreferenceManager().setShowOnboarding(false);
        a.getPreferenceManager().setAppOpenCount(0L);
    }

    @Test
    public void privacyAutoPublishPreferencesTest() {
        // Arrange
        PreferenceManager preferences = a.getPreferenceManager();
        Boolean initialValue = preferences.isUploadResults();

        // Act
        launchDashboard();
        onView(withId(R.id.settings)).perform(click());
        onView(withText(getResourceString(R.string.Settings_Privacy_Label))).perform(click());
        onView(withText(getResourceString(R.string.Settings_Sharing_UploadResults))).perform(click());

        // Assert
        assertEquals(initialValue, !preferences.isUploadResults());
    }

    public void launchDashboard() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        scenario = ActivityScenario.launch(intent);
    }
}
