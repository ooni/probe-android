package org.openobservatory.ooniprobe.common;

import android.content.SharedPreferences;
import android.text.format.DateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.engine.TestEngineInterface;
import org.openobservatory.ooniprobe.test.EngineProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.openobservatory.ooniprobe.common.PreferenceManager.AUTORUN_DATE;
import static org.openobservatory.ooniprobe.common.PreferenceManager.DELETE_JSON_DELAY;
import static org.openobservatory.ooniprobe.common.PreferenceManager.DELETE_JSON_KEY;
import static org.openobservatory.ooniprobe.common.PreferenceManager.UUID4;

public class PreferenceManagerTest extends RobolectricAbstractTest {

    private PreferenceManager pm;
    private SharedPreferences sharedPreferences;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        pm = new PreferenceManager(c);
        sharedPreferences = getDefaultSharedPreferences(c);
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        sharedPreferences.edit().clear().commit();
    }

    @Test
    public void testGetSetToken() {
        // Act
        String initialValue = pm.getToken();
        pm.setToken("abc");
        String value = pm.getToken();

        // Assert
        assertNull(initialValue);
        assertEquals("abc", value);
    }

    @Test
    public void testMaxRuntime() {
        // Act
        int value = pm.getMaxRuntime();
        boolean isEnabled = pm.isMaxRuntimeEnabled();

        // Assert
        assertEquals(90, value);
        assertTrue(isEnabled);
    }

    @Test
    public void testMaxRuntimeDisabled() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.max_runtime_enabled), false)
                .putString(c.getString(R.string.max_runtime), "91")
                .apply();

        // Act
        int value = pm.getMaxRuntime();
        boolean isEnabled = pm.isMaxRuntimeEnabled();

        // Assert
        assertFalse(isEnabled);
        assertEquals(PreferenceManager.MAX_RUNTIME_DISABLED.intValue(), value);
    }

    @Test
    public void testMaxRuntimeInvalid() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.max_runtime_enabled), true)
                .putString(c.getString(R.string.max_runtime), "invalid")
                .apply();

        // Act
        int value = pm.getMaxRuntime();

        // Assert
        assertEquals(90, value);
    }

    @Test
    public void testSendCrashGetSet() {
        // Act
        boolean original = pm.isSendCrash();
        pm.setSendCrash(true);
        boolean updated = pm.isSendCrash();

        // Assert
        assertFalse(original);
        assertTrue(updated);
    }

    @Test
    public void testShowOnBoardingGetSet() {
        // Act
        boolean original = pm.isShowOnboarding();
        pm.setShowOnboarding(false);
        boolean updated = pm.isShowOnboarding();

        // Assert
        assertTrue(original);
        assertFalse(updated);
    }

    @Test
    public void testAskNotificationDialog() {
        // Act
        boolean original = pm.isAskNotificationDialogDisabled();
        pm.disableAskNotificationDialog();
        boolean updated = pm.isAskNotificationDialogDisabled();

        // Assert
        assertFalse(original);
        assertTrue(updated);
    }

    @Test
    public void testIsDarkTheme() {
        // Act
        boolean original = pm.isDarkTheme();
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.theme_enabled), true)
                .apply();
        boolean value = pm.isDarkTheme();

        // Assert
        assertFalse(original);
        assertTrue(value);
    }

    @Test
    public void testNotificationsFromDialog() {
        // Act
        boolean isOriginal = pm.isNotifications();
        long appOpenOriginal = pm.getAppOpenCount();
        pm.setNotificationsFromDialog(true);
        boolean isUpdated = pm.isNotifications();
        long appOpenUpdated = pm.getAppOpenCount();

        // Assert
        assertFalse(isOriginal);
        assertTrue(isUpdated);
        assertEquals(0, appOpenOriginal);
        assertEquals(1, appOpenUpdated);
    }

    @Test
    public void testIsUploadResults() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.upload_results), false)
                .apply();

        // Act
        boolean value = pm.isUploadResults();

        // Assert
        assertFalse(value);
    }

    @Test
    public void testIsDebugLogs() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.debugLogs), false)
                .apply();

        // Act
        boolean value = pm.isDebugLogs();

        // Assert
        assertFalse(value);
    }

    @Test
    public void testAbstractTests() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.test_telegram), false)
                .putBoolean(c.getString(R.string.test_whatsapp), false)
                .putBoolean(c.getString(R.string.test_facebook_messenger), false)
                .putBoolean(c.getString(R.string.test_signal), false)
                .putBoolean(c.getString(R.string.test_psiphon), false)
                .putBoolean(c.getString(R.string.test_tor), false)
                .putBoolean(c.getString(R.string.test_riseupvpn), false)
                .putBoolean(c.getString(R.string.run_http_invalid_request_line), false)
                .putBoolean(c.getString(R.string.run_http_header_field_manipulation), false)
                .putBoolean(c.getString(R.string.run_ndt), false)
                .putBoolean(c.getString(R.string.run_dash), false)
                .apply();

        // Act // Assert
        assertFalse(pm.isTestTelegram());
        assertFalse(pm.isTestWhatsapp());
        assertFalse(pm.isTestFacebookMessenger());
        assertFalse(pm.isTestSignal());
        assertFalse(pm.isTestPsiphon());
        assertFalse(pm.isTestTor());
        assertFalse(pm.isTestRiseupVPN());
        assertFalse(pm.isRunHttpInvalidRequestLine());
        assertFalse(pm.isRunHttpHeaderFieldManipulation());
        assertFalse(pm.isRunNdt());
        assertFalse(pm.isRunDash());
    }

    @Test
    public void testEnabledCategoryArr() {
        // Arrange
        ArrayList<String> list = new ArrayList<>();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : c.getResources().getStringArray(R.array.CategoryCodes)) {
            editor.putBoolean(key, true);
            list.add(key);
        }

        editor.putBoolean(list.get(0), false);
        editor.putBoolean(list.get(1), false);
        editor.commit();

        list.remove(0);
        list.remove(0);

        // Act
        int count = pm.countEnabledCategory();
        List<String> categories = pm.getEnabledCategoryArr();

        // Assert
        assertEquals(count, list.size());
        assertArrayEquals(categories.toArray(), list.toArray());
    }

    @Test
    public void testCanCallDeleteJson() {
        // Act
        sharedPreferences.edit()
                .remove(DELETE_JSON_KEY)
                .commit(); // This value gets set on Application create
        boolean defaultValue = pm.canCallDeleteJson();
        sharedPreferences.edit()
                .putLong(DELETE_JSON_KEY, DELETE_JSON_DELAY + 1L)
                .apply();
        boolean original = pm.canCallDeleteJson();
        pm.setLastCalled();
        boolean updated = pm.canCallDeleteJson();

        // Assert
        assertTrue(defaultValue);
        assertTrue(original);
        assertFalse(updated);
    }

    @Test
    public void testGetOrGenerateUUID4() {
        // Arrange
        OONISession mockSession = mock(OONISession.class);
        EngineProvider.engineInterface = new TestEngineInterface(mockSession);

        // Act
        String original = pm.getOrGenerateUUID4();

        sharedPreferences.edit()
                .putString(UUID4, "abc")
                .apply();

        String updated = pm.getOrGenerateUUID4();

        // Assert
        assertEquals("UUID4", original);
        assertEquals("abc", updated);
    }

    @Test
    public void testAppOpenCount() {
        // Act
        long original = pm.getAppOpenCount();
        pm.incrementAppOpenCount();
        pm.incrementAppOpenCount();
        long updated = pm.getAppOpenCount();

        // Assert
        assertEquals(original, 0L);
        assertEquals(updated, 2L);
    }

    @Test
    public void testAutomaticTest() {
        // Arrange
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.automated_testing_enabled), true)
                .apply();

        // Act
        boolean original = pm.isAutomaticTestEnabled();
        pm.disableAutomaticTest();
        boolean updated = pm.isAutomaticTestEnabled();

        // Assert
        assertTrue(original);
        assertFalse(updated);
    }

    @Test
    public void testTestWifiOnly() {
        // Act
        boolean original = pm.testWifiOnly();
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.automated_testing_wifionly), false)
                .apply();
        boolean value = pm.testWifiOnly();

        // Assert
        assertTrue(original);
        assertFalse(value);
    }

    @Test
    public void testTestChargingOnly() {
        // Act
        boolean original = pm.testChargingOnly();
        sharedPreferences.edit()
                .putBoolean(c.getString(R.string.automated_testing_charging), false)
                .apply();
        boolean value = pm.testChargingOnly();

        // Assert
        assertTrue(original);
        assertFalse(value);
    }

    @Test
    public void testAutorun() {
        // Act
        long original = pm.getAutorun();
        pm.incrementAutorun();
        long value = pm.getAutorun();

        // Assert
        assertEquals(0L, original);
        assertEquals(1L, value);
    }

    @Test
    public void testAutorunDate() {
        // Arrange
        String updatedDate = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), System.currentTimeMillis()).toString();

        sharedPreferences.edit()
                .putLong(AUTORUN_DATE, 0L)
                .apply();

        // Act
        String original = pm.getAutorunDate();
        pm.updateAutorunDate();
        String updated = pm.getAutorunDate();

        // Assert
        assertEquals(c.getString(R.string.Dashboard_Overview_LastRun_Never), original);
        assertEquals(updatedDate, updated);
    }
}
