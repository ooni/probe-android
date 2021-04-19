package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.ArrayList;

public class ExperimentalSuite extends AbstractSuite {
    public static final String NAME = "experimental";

    public ExperimentalSuite() {
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
                R.string.TestResults_NotAvailable);
    }

    @Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
        if (super.getTestList(pm) == null) {
            ArrayList<AbstractTest> list = new ArrayList<>();
            //list.add(new Experimental("dnscheck"));
            list.add(new Experimental("stunreachability"));
            super.setTestList(list.toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }}
