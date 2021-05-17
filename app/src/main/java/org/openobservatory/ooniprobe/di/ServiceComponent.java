package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.service.RunTestJobService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.di.annotations.PerService;

import dagger.Subcomponent;

@PerService
@Subcomponent()
public interface ServiceComponent {
    void inject(RunTestJobService runTestJobService);
}
public interface ServiceComponent {
    void inject(ServiceUtil.Dependencies deps);
    void inject(RunTestJobService service);
}