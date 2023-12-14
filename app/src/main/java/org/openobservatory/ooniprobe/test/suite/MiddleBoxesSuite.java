package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;

import java.util.ArrayList;

/**
 * @deprecated It is not possible to run a MiddleBoxesSuite anymore
 * The HttpHeaderFieldManipulation and HttpInvalidRequestLine tests
 * are being ran inside the PerformanceSuite
 */
@Deprecated
public class MiddleBoxesSuite extends AbstractSuite {
    public static final String NAME = "middle_boxes";

    public MiddleBoxesSuite() {
        super(NAME,
                R.string.Test_Middleboxes_Fullname,
                R.string.Dashboard_Middleboxes_Card_Description,
                R.drawable.test_middle_boxes,
                R.drawable.test_middle_boxes_24,
                R.color.color_violet8,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
                R.style.Theme_MaterialComponents_NoActionBar_App_MiddleBoxes,
                R.string.Dashboard_Middleboxes_Overview_Paragraph,
                "anim/middle_boxes.json",
                R.string.small_datausage);
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
