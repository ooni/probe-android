package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.service.RunTestService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.di.annotations.PerService;

import dagger.Subcomponent;

@PerService
@Subcomponent()
public interface ServiceComponent {
    void inject(ServiceUtil.Dependencies deps);
    void inject(RunTestService service);
}