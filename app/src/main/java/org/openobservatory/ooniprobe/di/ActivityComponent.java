package org.openobservatory.ooniprobe.di;


import org.openobservatory.ooniprobe.activity.CustomWebsiteActivity;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.MeasurementDetailActivity;
import org.openobservatory.ooniprobe.activity.OoniRunActivity;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.activity.TextActivity;
import org.openobservatory.ooniprobe.di.annotations.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent()
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(OoniRunActivity activity);
    void inject(MeasurementDetailActivity activity);
    void inject(ResultDetailActivity activity);
    void inject(RunningActivity activity);
    void inject(TextActivity activity);
    void inject(CustomWebsiteActivity activity);
    void inject(OverviewActivity activity);
}