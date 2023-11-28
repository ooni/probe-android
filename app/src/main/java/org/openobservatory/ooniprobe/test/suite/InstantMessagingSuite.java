package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.util.ArrayList;

/**
 * Represents a suite of tests that can be run together.
 *
 * @deprecated Better represented by {@link DynamicTestSuite()} which acts as a wrapper for {@link AbstractTest}
 * and more versatile for representing a suite of tests moving forward.
 * To be replaced by {@link DynamicTestSuite()}
 */
public class InstantMessagingSuite extends AbstractSuite {
    public static final String NAME = "instant_messaging";

    public InstantMessagingSuite() {
        super(NAME,
                R.string.Test_InstantMessaging_Fullname,
                R.string.Dashboard_InstantMessaging_Card_Description,
                R.drawable.test_instant_messaging,
                R.drawable.test_instant_messaging_24,
                R.color.color_cyan6,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_InstantMessaging,
                R.style.Theme_MaterialComponents_NoActionBar_App_InstantMessaging,
                R.string.Dashboard_InstantMessaging_Overview_Paragraph,
                "anim/instant_messaging.json",
                R.string.small_datausage);
    }

    public static InstantMessagingSuite initForAutoRun() {
        InstantMessagingSuite suite = new InstantMessagingSuite();
        suite.setAutoRun(true);
        return suite;
    }

    /**
     * NOTE: The checks to determine if a test is enabled before adding it to the list is removed to make way for
     * more dynamic test suites. This is because the tests are now enabled/disabled in
     * the {@link org.openobservatory.ooniprobe.activity.runtests.RunTestsActivity} and not statically in the code.
     * @param pm
     * @return The list of tests that are part of this suite
     */
    @Override
    public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
        if (super.getTestList(pm) == null) {
            ArrayList<AbstractTest> list = new ArrayList<>();
            list.add(new Whatsapp());
            list.add(new Telegram());
            list.add(new FacebookMessenger());
            list.add(new Signal());
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }
}
