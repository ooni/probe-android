package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.di.DaggerTestAppComponent;

public class TestApplication extends Application {

    @Override
    protected AppComponent buildDagger() {
        return DaggerTestAppComponent.builder().testAppModule(new TestAppModule(this)).build();
    }
}
