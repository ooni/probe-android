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

    public AbstractSuite generate(
            OONICheckInConfig config
    ) {

        try {
            OONISession session = EngineProvider.get().newSession(
                    EngineProvider.get().getDefaultSessionConfig(
                            app,
                            String.join("-",BuildConfig.SOFTWARE_NAME, AbstractTest.UNATTENDED),
                            BuildConfig.VERSION_NAME,
                            new LoggerArray(),
                            pm.getProxyURL()
                    )
            );
            OONIContext ooniContext = session.newContextWithTimeout(30);
            session.maybeUpdateResources(ooniContext);

            OONICheckInResults results = session.checkIn(ooniContext, config);

            if (results.getWebConnectivity() != null) {
                List<String> inputs = new ArrayList<>();
                for (OONIURLInfo url : results.getWebConnectivity().getUrls()) {
                    inputs.add(url.getUrl());
                }

                markAsRan();

                return AbstractSuite.getSuite(
                        app,
                        "web_connectivity",
                        inputs,
                        AbstractTest.AUTORUN
                );
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
            return null;
        }
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

    private void markAsRan() {
        pm.updateAutorunDate();
        pm.incrementAutorun();
    }
}
