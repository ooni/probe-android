package org.openobservatory.ooniprobe.factory;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.di.AppComponent;
import org.openobservatory.ooniprobe.di.TestAppModule;
import org.openobservatory.ooniprobe.di.DaggerTestAppComponent;

public class TestApplication extends Application {

    @Override
    protected AppComponent buildDagger() {
        return DaggerTestAppComponent.builder().testAppModule(new TestAppModule(this)).build();
    }
}
