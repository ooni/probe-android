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
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.test.EngineProvider;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

import javax.inject.Inject;

public class GenerateAutoRunServiceSuite {

    private final PreferenceManager pm;
    private final Application app;

    @Inject
    GenerateAutoRunServiceSuite(Application application, PreferenceManager pm) {
        this.pm = pm;
        this.app = application;
    }

    public AbstractSuite generate(OONICheckInConfig config, Boolean isWifi, Boolean isCharging) {
        if (!shouldStart(isWifi, isCharging)) {
            return null;
        }

        try {
            OONISession session = EngineProvider.get().newSession(
                    EngineProvider.get().getDefaultSessionConfig(
                            app, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME, new LoggerArray())
            );
            OONIContext ooniContext = session.newContextWithTimeout(30);
            session.maybeUpdateResources(ooniContext);

            OONICheckInResults results = session.checkIn(ooniContext, config);

            if (results.getWebConnectivity() != null) {
                ArrayList<String> inputs = new ArrayList<>();
                for (OONIURLInfo url : results.getWebConnectivity().getUrls()) {
                    inputs.add(url.getUrl());
                }

                markAsRan();

                return AbstractSuite.getSuite(
                        app,
                        "web_connectivity",
                        inputs,
                        "autorun"
                );
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
            return null;
        }
    }

    private boolean shouldStart(Boolean isWifi, Boolean isCharging) {
        if (pm.testWifiOnly() && !isWifi)
            return false;
        if (pm.testChargingOnly() && !isCharging)
            return false;

        return true;
    }

    private void markAsRan() {
        pm.updateAutorunDate();
        pm.incrementAutorun();
    }
}
