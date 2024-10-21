package org.openobservatory.ooniprobe.domain;

import static org.openobservatory.ooniprobe.test.suite.AbstractSuiteExtensionsKt.getSuite;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import javax.inject.Inject;

public class GenerateAutoRunServiceSuite {

    private final PreferenceManager pm;
    private final Application app;

    @Inject
    GenerateAutoRunServiceSuite(Application application, PreferenceManager pm) {
        this.pm = pm;
        this.app = application;
    }

    public AbstractSuite generate() {
        return getSuite(app, WebConnectivity.NAME, null, AbstractTest.AUTORUN);
    }


    public boolean shouldStart(Boolean isWifi, Boolean isCharging, Boolean isVPNInUse) {
        if (pm.testWifiOnly() && !isWifi)
            return false;
        if (pm.testChargingOnly() && !isCharging)
            return false;
        if (ReachabilityManager.getChargingLevel(app) < 20 && !isCharging)
            return false;
        if (isVPNInUse) {
            return false;
        }

        return true;
    }

    public void markAsRan() {
        pm.updateAutorunDate();
        pm.incrementAutorun();
    }
}
