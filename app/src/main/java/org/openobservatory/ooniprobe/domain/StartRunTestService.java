package org.openobservatory.ooniprobe.domain;

import android.content.Context;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONICheckInConfig;
import org.openobservatory.engine.OONICheckInResults;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.EngineProvider;

import java.util.ArrayList;

import javax.inject.Inject;

public class StartRunTestService {

    private final PreferenceManager pm;
    private final Context context;

    @Inject
    StartRunTestService(PreferenceManager pm, Context context) {
        this.pm = pm;
        this.context = context;
    }

    public boolean shouldStart(Boolean isWifi, Boolean isCharging) {
        if (pm.testWifiOnly() && !isWifi)
            return false;
        if (pm.testChargingOnly() && !isCharging)
            return false;

        return true;
    }

    public ArrayList<String> getUrls(Boolean isWifi, Boolean isCharging) throws Exception {
        ArrayList<String> inputs = new ArrayList<>();

        OONISession session = EngineProvider.get().newSession(
                EngineProvider.get().getDefaultSessionConfig(
                        context, BuildConfig.SOFTWARE_NAME, BuildConfig.VERSION_NAME, new LoggerArray(), pm.getProxyURL())
        );
        OONIContext ooniContext = session.newContextWithTimeout(30);
        session.maybeUpdateResources(ooniContext);
        OONICheckInConfig config = new OONICheckInConfig(
                BuildConfig.SOFTWARE_NAME,
                BuildConfig.VERSION_NAME,
                isWifi,
                isCharging,
                pm.getEnabledCategoryArr().toArray(new String[0]));
        OONICheckInResults results = session.checkIn(ooniContext, config);
        if (results.getWebConnectivity() != null) {
            for (OONIURLInfo url : results.getWebConnectivity().getUrls()) {
                inputs.add(url.getUrl());
            }
        }

        return inputs;
    }

    public void startedRun() {
        pm.updateAutorunDate();
        pm.incrementAutorun();
    }
}
