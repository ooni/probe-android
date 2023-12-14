package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a suite of tests that can be run together.
 *
 * @deprecated Better represented by {@link DynamicTestSuite()} which acts as a wrapper for {@link AbstractTest}
 * and more versatile for representing a suite of tests moving forward.
 * To be replaced by {@link DynamicTestSuite()}
 */
public class ExperimentalSuite extends AbstractSuite {
    public static final String NAME = "experimental";

    public ExperimentalSuite() {
        super(NAME,
                R.string.Test_Experimental_Fullname,
                R.string.Dashboard_Experimental_Card_Description,
                R.drawable.test_experimental,
                R.drawable.test_experimental_24,
                R.color.color_gray7_1,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental,
                R.style.Theme_MaterialComponents_NoActionBar_App_Experimental,
                R.string.Dashboard_Experimental_Overview_Paragraph,
                "anim/experimental.json",
                R.string.TestResults_NotAvailable);
    }

    public static ExperimentalSuite initForAutoRun() {
        ExperimentalSuite suite = new ExperimentalSuite();
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
            if (pm == null || pm.isExperimentalOn()){
                list.add(new Experimental("stunreachability"));
                list.add(new Experimental("dnscheck"));
                list.add(new Experimental("riseupvpn"));
                list.add(new Experimental("echcheck"));
				if ((pm == null || pm.isLongRunningTestsInForeground()) || getAutoRun()){
                    Collections.addAll(list, longRunningTests());
                }
            }
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }

    public AbstractTest[] longRunningTests() {
        return new AbstractTest[]{new Experimental("torsf"), new Experimental("vanilla_tor")};
    }

}
