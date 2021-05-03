package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.Application;

public class TestApplication extends Application {

    @Override
    protected AppComponent buildDagger() {
        return DaggerTestAppComponent.builder().testAppModule(new TestAppModule(this)).build();
    }
}