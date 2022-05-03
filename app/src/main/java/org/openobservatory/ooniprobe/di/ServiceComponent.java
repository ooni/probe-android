package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.common.service.RunTestJobService;
import org.openobservatory.ooniprobe.common.service.ServiceUtil;
import org.openobservatory.ooniprobe.di.annotations.PerService;
import org.openobservatory.ooniprobe.receiver.ConnectivityReceiver;

import dagger.Subcomponent;

@PerService
@Subcomponent()
public interface ServiceComponent {
    void inject(ResubmitTask.Dependencies dependencies);
    void inject(RunTestJobService service);
    void inject(ConnectivityReceiver connectivityReceiver);
    void inject(ServiceUtil.Dependencies dependencies);
}