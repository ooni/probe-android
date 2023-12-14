package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.Tor;

import java.util.ArrayList;

/**
 * Represents a suite of tests that can be run together.
 *
 * @deprecated Better represented by {@link DynamicTestSuite()} which acts as a wrapper for {@link AbstractTest}
 * and more versatile for representing a suite of tests moving forward.
 * To be replaced by {@link DynamicTestSuite()}
 */
public class CircumventionSuite extends AbstractSuite {
    public static final String NAME = "circumvention";

    public CircumventionSuite() {
        super(NAME,
                R.string.Test_Circumvention_Fullname,
                R.string.Dashboard_Circumvention_Card_Description,
                R.drawable.test_circumvention,
                R.drawable.test_circumvention_24,
                R.color.color_pink6,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Circumvention,
                R.style.Theme_MaterialComponents_NoActionBar_App_Circumvention,
                R.string.Dashboard_Circumvention_Overview_Paragraph,
                "anim/circumvention.json",
                R.string.small_datausage);
    }


    public static CircumventionSuite initForAutoRun() {
        CircumventionSuite suite = new CircumventionSuite();
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
            list.add(new Psiphon());
            list.add(new Tor());
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }

}
