package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface AppComponent {

    ActivityComponent activityComponent();
    ServiceComponent serviceComponent();
    void inject(Application app);

}