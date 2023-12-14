package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.ArrayList;

/**
 * Represents a suite of tests that can be run together.
 *
 * @deprecated Better represented by {@link DynamicTestSuite()} which acts as a wrapper for {@link AbstractTest}
 * and more versatile for representing a suite of tests moving forward.
 * To be replaced by {@link DynamicTestSuite()}
 */
public class PerformanceSuite extends AbstractSuite {
    public static final String NAME = "performance";

    public PerformanceSuite() {
        super(NAME,
                R.string.Test_Performance_Fullname,
                R.string.Dashboard_Performance_Card_Description,
                R.drawable.test_performance,
                R.drawable.test_performance_24,
                R.color.color_fuchsia6,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Performance,
                R.style.Theme_MaterialComponents_NoActionBar_App_Performance,
                R.string.Dashboard_Performance_Overview_Paragraph_Updated,
                "anim/performance.json",
                R.string.performance_datausage);
    }

    public static PerformanceSuite initForAutoRun() {
        PerformanceSuite suite = new PerformanceSuite();
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
            list.add(new Ndt());
            list.add(new Dash());
            list.add(new HttpHeaderFieldManipulation());
            list.add(new HttpInvalidRequestLine());
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }
}
