package org.openobservatory.ooniprobe.di;


import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.di.annotations.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent()
public interface ActivityComponent {
    void inject(MainActivity activity);
}