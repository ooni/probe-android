
package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.Application;

import dagger.Module;

@Module
public class TestAppModule extends ApplicationModule {

    private static final String CLIENT_URL = "https://ams-pg.ooni.org";

    public TestAppModule(Application application) {
        super(application);
    }

    @Override
    protected String getApiUrl() {
        return CLIENT_URL;
    }
}