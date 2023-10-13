package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.ArrayList;

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


    @Override
    public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
        if (super.getTestList(pm) == null) {
            ArrayList<AbstractTest> list = new ArrayList<>();
            if (pm == null || pm.isExperimentalOn()){
                list.add(new Experimental("echcheck"));
                list.add(new Experimental("stunreachability"));
                list.add(new Experimental("dnscheck"));
				if ((pm == null || pm.isLongRunningTestsInForeground()) || getAutoRun()){
					list.add(new Experimental("torsf"));
					list.add(new Experimental("vanilla_tor"));
                }
            }
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }

}
