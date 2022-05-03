package org.openobservatory.ooniprobe.common;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONIGeolocateResults;
import org.openobservatory.engine.OONISession;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.test.EngineProvider;

import java.util.Objects;

import javax.inject.Inject;


public class NetworkChangeProcessor {

    private final PreferenceManager pm;
    private final Application app;

    @Inject
    NetworkChangeProcessor(Application application, PreferenceManager pm) {
        this.pm = pm;
        this.app = application;
    }


    public void processNetworkPossibleNetworkChange() {
        try {
            OONISession session = EngineProvider.get().newSession(
                    EngineProvider.get().getDefaultSessionConfig(
                            app,
                            BuildConfig.SOFTWARE_NAME,
                            BuildConfig.VERSION_NAME,
                            new LoggerArray(),
                            pm.getProxyURL()
                    )
            );
            OONIGeolocateResults geoLocateResults = session.geolocate(session.newContext());
            System.out.println(pm.getLastKnownNetwork());
            if (!Objects.equals(pm.getLastKnownNetwork(), geoLocateResults.getASN())) {
                pm.setLastKnownNetwork(geoLocateResults.getASN());
                ServiceUtil.scheduleJob(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }
}
