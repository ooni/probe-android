package org.openobservatory.ooniprobe.test.suite;

import android.content.res.Resources;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.Tor;

import java.util.ArrayList;

public class CircumventionSuite extends AbstractSuite {
    public static final String NAME = "circumvention";

    public CircumventionSuite(Resources resources){
        super(NAME,
                resources.getString(R.string.Test_Circumvention_Fullname),
                resources.getString(R.string.Dashboard_Circumvention_Card_Description),
                R.drawable.test_circumvention,
                R.drawable.test_circumvention_24,
                R.color.color_pink6,
                R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Circumvention,
                R.style.Theme_MaterialComponents_NoActionBar_App_Circumvention,
                resources.getString(R.string.Dashboard_Circumvention_Overview_Paragraph),
                "anim/circumvention.json",
                R.string.small_datausage);
    }


    public static CircumventionSuite initForAutoRun(Resources resources) {
        CircumventionSuite suite = new CircumventionSuite(resources);
        suite.setAutoRun(true);
        return suite;
    }

    @Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
        if (super.getTestList(pm) == null) {
            ArrayList<AbstractTest> list = new ArrayList<>();
            if (pm == null || pm.isTestPsiphon())
                list.add(new Psiphon());
            if (pm == null || pm.isTestTor())
                list.add(new Tor());
            /* TODO (aanorbel): Riseup VPN Disabled.
                The riseupvpn experiment has been quite flaky for quite some time.
                To be enabled only when test is fixed or removed if deemed necessary.
                if (pm == null || pm.isTestRiseupVPN())
                list.add(new RiseupVPN());*/
            super.setTestList(Lists.transform(list, test -> {
                if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
                return test;
            }).toArray(new AbstractTest[0]));
        }
        return super.getTestList(pm);
    }

}
