package org.openobservatory.ooniprobe.test.suite;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.ArrayList;

public class ExperimentalSuite extends AbstractSuite {
    public static final String NAME = "experimental";

    @SuppressLint("StringFormatInvalid")
    public ExperimentalSuite(Resources resources) {

          super(NAME,
                resources.getString(R.string.Test_Experimental_Fullname),
                resources.getString(R.string.Dashboard_Experimental_Card_Description),
                R.drawable.test_experimental,
                R.drawable.test_experimental_24,
                R.color.color_gray7_1,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental,
                R.style.Theme_MaterialComponents_NoActionBar_App_Experimental,
                resources.getString(R.string.Dashboard_Experimental_Overview_Paragraph,"\n\n* [STUN Reachability](https://github.com/ooni/spec/blob/master/nettests/ts-025-stun-reachability.md)" +
                        "\n\n* [DNS Check](https://github.com/ooni/spec/blob/master/nettests/ts-028-dnscheck.md)" +
                        "\n\n* [ECH Check](https://github.com/ooni/spec/blob/master/nettests/ts-039-echcheck.md)" +
                        "\n\n* [Tor Snowflake](https://ooni.org/nettest/tor-snowflake/) "+ String.format(" ( %s )",resources.getString(R.string.Settings_TestOptions_LongRunningTest))+
                        "\n\n* [Vanilla Tor](https://github.com/ooni/spec/blob/master/nettests/ts-016-vanilla-tor.md) " + String.format(" ( %s )",resources.getString(R.string.Settings_TestOptions_LongRunningTest))),
                "anim/experimental.json",
                R.string.TestResults_NotAvailable);
    }

    public static ExperimentalSuite initForAutoRun(Resources resources) {
        ExperimentalSuite suite = new ExperimentalSuite(resources);
        suite.setAutoRun(true);
        return suite;
    }


    @Override
    public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
        if (super.getTestList(pm) == null) {
            ArrayList<AbstractTest> list = new ArrayList<>();
            if (pm == null || pm.isExperimentalOn()){
                list.add(new Experimental("stunreachability"));
                list.add(new Experimental("dnscheck"));
                list.add(new Experimental("echcheck"));
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
