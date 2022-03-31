package org.openobservatory.ooniprobe.domain;

import org.openobservatory.engine.LoggerArray;
import org.openobservatory.engine.OONIContext;
import org.openobservatory.engine.OONISession;
import org.openobservatory.engine.OONIURLListConfig;
import org.openobservatory.engine.OONIURLListResult;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.test.EngineProvider;

import javax.inject.Inject;

public class UrlsManager {


    private final Application application;

    @Inject
    UrlsManager(Application application) {
        this.application = application;
    }

    public OONIURLListResult downloadUrls() throws Exception {
        OONISession session = EngineProvider.get().newSession(EngineProvider.get().getDefaultSessionConfig(
                application,
                BuildConfig.SOFTWARE_NAME,
                BuildConfig.VERSION_NAME,
                new LoggerArray(),
                application.getPreferenceManager().getProxyURL()
        ));
        OONIContext ooniContext = session.newContextWithTimeout(30);

        ThirdPartyServices.addLogExtra("ooniContext", application.getGson().toJson(ooniContext));

        session.maybeUpdateResources(ooniContext);
        OONIURLListConfig config = new OONIURLListConfig();
        config.setCategories(application.getPreferenceManager().getEnabledCategoryArr().toArray(new String[0]));

        ThirdPartyServices.addLogExtra("config", application.getGson().toJson(config));

        OONIURLListResult results = session.fetchURLList(ooniContext, config);

        ThirdPartyServices.addLogExtra("results", application.getGson().toJson(results));
        return results;
    }
}
