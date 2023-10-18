package org.openobservatory.ooniprobe.domain;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.ArrayList;
import java.util.List;

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

        return AbstractSuite.getSuite(
                app,
                "web_connectivity",
                null,
                AbstractTest.AUTORUN
        );
    }


    public boolean shouldStart(Boolean isWifi, Boolean isCharging, Boolean isVPNInUse) {
        if (pm.testWifiOnly() && !isWifi)
            return false;
        if (pm.testChargingOnly() && !isCharging)
            return false;
        if(ReachabilityManager.getChargingLevel(app) < 20 && !isCharging)
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
