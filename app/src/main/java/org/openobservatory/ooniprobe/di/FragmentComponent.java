package org.openobservatory.ooniprobe.di;

import org.openobservatory.ooniprobe.di.annotations.PerActivity;
import org.openobservatory.ooniprobe.fragment.onboarding.Onboarding3Fragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent()
public interface FragmentComponent {
    void inject(Onboarding3Fragment fragment);
}
