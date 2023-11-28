package org.openobservatory.ooniprobe.test.suite;

import org.junit.Test;
import org.openobservatory.ooniprobe.TestApplicationProvider;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstantMessagingSuiteTest {

    private final InstantMessagingSuite suite = new InstantMessagingSuite(TestApplicationProvider.app().getResources());
    private final PreferenceManager pm = mock(PreferenceManager.class);

    @Test
    public void getTestList_empty() {
        when(pm.isTestWhatsapp()).thenReturn(false);
        when(pm.isTestTelegram()).thenReturn(false);
        when(pm.isTestFacebookMessenger()).thenReturn(false);
        when(pm.isTestSignal()).thenReturn(false);

        AbstractTest[] tests = suite.getTestList(pm);

        assertEquals(0, tests.length);
    }

    @Test
    public void getTestList_full() {
        when(pm.isTestWhatsapp()).thenReturn(true);
        when(pm.isTestTelegram()).thenReturn(true);
        when(pm.isTestFacebookMessenger()).thenReturn(true);
        when(pm.isTestSignal()).thenReturn(true);

        List<AbstractTest> tests = Arrays.asList(suite.getTestList(pm));

        assertEquals(4, tests.size());
        assertTrue(findTestClass(tests, Whatsapp.class));
        assertTrue(findTestClass(tests, Telegram.class));
        assertTrue(findTestClass(tests, FacebookMessenger.class));
        assertTrue(findTestClass(tests, Signal.class));
    }

    private boolean findTestClass(List<AbstractTest> tests, Class<? extends AbstractTest> klass) {
        return tests.stream().map(AbstractTest::getClass).anyMatch(c -> c == klass);
    }
}
